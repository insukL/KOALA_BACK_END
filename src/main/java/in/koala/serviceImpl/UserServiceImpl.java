package in.koala.serviceImpl;

import in.koala.domain.AuthEmail;
import in.koala.domain.User;
import in.koala.enums.ErrorMessage;
import in.koala.enums.SnsType;
import in.koala.enums.TokenType;
import in.koala.exception.NonCriticalException;
import in.koala.mapper.AuthEmailMapper;
import in.koala.mapper.UserMapper;
import in.koala.service.sns.SnsLogin;
import in.koala.service.UserService;
import in.koala.util.Jwt;
import in.koala.util.SesSender;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final AuthEmailMapper authEmailMapper;
    private final HttpServletResponse response;
    private final Jwt jwt;
    // list 형식으로 주입받게 되면 해당 인터페이스를 구현하는 모든 클래스를 주입받을 수 있다.
    private final List<SnsLogin> snsLoginList;
    private final SesSender sesSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Override
    public String test() {
        return userMapper.test();
    }

    @Override
    public Map<String, String> snsLogin(String code, SnsType snsType) throws Exception {
        // sns 별 인터페이스 구현체 변경
        SnsLogin snsLogin = initSnsService(snsType);

        // snsLogin 에 유저 정보요청
        Map<String, String> userProfile = snsLogin.requestUserProfile(code);

        // 받은 정보를 이용하여 User domain 생성
        User snsUser = User.builder()
                .account(userProfile.get("account"))
                .sns_email(userProfile.get("sns_email"))
                .profile(userProfile.get("profile"))
                .nickname(userProfile.get("nickname"))
                .user_type((short) 1)
                .build();

        Long id = userMapper.getIdByAccount(snsUser.getAccount());

        // 해당 유저가 처음 sns 로그인을 요청한다면 회원가입
        if(id == null) userMapper.snsSignUp(snsUser);

        return generateAccessAndRefreshToken(id);
    }

    // sns 별 oauth2 로그인 요청을 하는 메서드, 해당 api 요청한 페이지를 redirect 시킨다.
    // swagger 에서는 작동하지 않는다. 앱에서 작동여부도 확인해봐야 함
    @Override
    public void requestSnsLogin(SnsType snsType) throws Exception {
        // snsType 에 맞게 구현체를 결정하는 메소드
        SnsLogin snsLogin = initSnsService(snsType);

        //System.out.println(uri);
        response.sendRedirect(snsLogin.getRedirectUri());
    }

    @Override
    public User signUp(User user) {
        User selectUser = userMapper.getUserByAccount(user.getAccount());

        // 해당 계정명이 이미 존재한다면 예외처리
        if(selectUser != null) throw new NonCriticalException(ErrorMessage.DUPLICATED_ACCOUNT_EXCEPTION);

        // 해당 닉네임이 이미 존재한다면 예외처리
        if(userMapper.checkNickname(user.getNickname()) >= 1) throw new NonCriticalException(ErrorMessage.DUPLICATED_NICKNAME_EXCEPTION);

        // 비밀번호 단방향 암호화
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        
        userMapper.signUp(user);

        return userMapper.getUserById(user.getId());
    }

    @Override
    public Map<String, String> login(User user) {
        User loginUser = userMapper.getUserPassword(user.getAccount());

        // 해당 계정이 존재하지 않는다면 예외처리
        if(loginUser == null) throw new NonCriticalException(ErrorMessage.ACCOUNT_NOT_EXIST);

        // 계정은 존재하나 비밀번호가 존재하지 않는다면 예외처리
        if(!BCrypt.checkpw(user.getPassword(), loginUser.getPassword())) throw new NonCriticalException(ErrorMessage.WRONG_PASSWORD_EXCEPTION);

        return generateAccessAndRefreshToken(loginUser.getId());
    }

    @Override
    public User getLoginUserInfo() {
        Long id = this.getLoginUserIdFromJwt(TokenType.ACCESS);

        User user = userMapper.getUserById(id);

        if (user == null) throw new NonCriticalException(ErrorMessage.USER_NOT_EXIST);

        return user;
    }

    @Override
    public void updateNickname(String nickname) {
        if (userMapper.checkNickname(nickname) > 0) {
            throw new NonCriticalException(ErrorMessage.DUPLICATED_NICKNAME_EXCEPTION);
        }

        User updateUser = new User();

        updateUser.setNickname(nickname);
        updateUser.setId(this.getLoginUserIdFromJwt(TokenType.ACCESS));

        if(userMapper.getUserById(updateUser.getId()) == null){
            throw new NonCriticalException(ErrorMessage.USER_NOT_EXIST);
        }

        userMapper.updateNickname(updateUser);

        return;
    }

    @Override
    public Boolean checkNickname(String nickname) {
        if (userMapper.checkNickname(nickname) > 0) {
            throw new NonCriticalException(ErrorMessage.DUPLICATED_NICKNAME_EXCEPTION);

        } else{
            return true;
        }
    }

    @Override
    public Map<String, String> refresh() {
        Long id = this.getLoginUserIdFromJwt(TokenType.REFRESH);

        if(userMapper.getUserById(id) == null) {
            throw new NonCriticalException(ErrorMessage.USER_NOT_EXIST);
        }

        return this.generateAccessAndRefreshToken(id);
    }

    @Override
    public void sendEmail(AuthEmail authEmail) {

        Long userId = this.getLoginUserIdFromJwt(TokenType.ACCESS);

        authEmail.setUserId(userId);

        // 당일 전송한 이메일의 횟수가 5를 초과했는지 확인하는 메소드
        if(isEmailSentNumExceed(authEmail)){
            throw new NonCriticalException(ErrorMessage.EMAIL_SEND_EXCEED_EXCEPTION);
        }

        String secret = "";
        Random random = new Random();

        // 난수 생성
        for(int i = 0; i < 5; i++){
            secret += random.nextInt(10);
        }

        Context context = new Context();
        context.setVariable("secret", secret);

        String body = springTemplateEngine.process("authenticationEmail", context);
        sesSender.sendMail("no-reply@koala.im", authEmail.getEmail(),
                "KOALA 서비스 인증 메일입니다.", body);

        authEmailMapper.expirePastAuthEmail(authEmail);
        authEmailMapper.insertAuthEmail(authEmail);

        return;
    }

    private boolean isEmailSentNumExceed(AuthEmail authEmail){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year,month,day,0,0 ,0); // 해당 날짜의 00시 00분 00초
        Timestamp start =  new Timestamp(calendar.getTimeInMillis());
        calendar.set(year,month,day,23,59 ,59); // 해당 날짜의 23시 59분 59초
        Timestamp end =  new Timestamp(calendar.getTimeInMillis());

        if(authEmailMapper.getAuthEmailNumByUserIdAndType(authEmail.getUserId(), authEmail.getType(), start, end) > 5){
            return true;
        }

        return false;
    }

    private Long getLoginUserIdFromJwt(TokenType tokenType){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        jwt.isValid(token, tokenType);

        return Long.valueOf(String.valueOf(jwt.getClaimsFromJwtToken(token, tokenType).get("id")));
    }

    private Map<String, String> generateAccessAndRefreshToken(Long id){
        Map<String, String> token = new HashMap<>();

        token.put("access_token", jwt.generateToken(id, TokenType.ACCESS));
        token.put("refresh_token", jwt.generateToken(id, TokenType.REFRESH));

        return token;
    }

    // 로그인 요청이 들어온 sns 에 맞춰 SnsLogin 인터페이스 구현체 결정
    private SnsLogin initSnsService(SnsType snsType){

        for(val snsLogin : snsLoginList){
            if(snsLogin.getSnsType().equals(snsType)) return snsLogin;
        }

        throw new NonCriticalException(ErrorMessage.SNSTYPE_NOT_VALID);
    }
}
