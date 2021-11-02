package in.koala.serviceImpl;

import in.koala.domain.User;
import in.koala.enums.ErrorMessage;
import in.koala.enums.SnsType;
import in.koala.enums.TokenType;
import in.koala.exception.NonCriticalException;
import in.koala.mapper.UserMapper;
import in.koala.service.sns.SnsLogin;
import in.koala.service.UserService;
import in.koala.util.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final HttpServletResponse response;
    private final Jwt jwt;
    // list 형식으로 주입받게 되면 해당 인터페이스를 구현하는 모든 클래스를 주입받을 수 있다.
    private final List<SnsLogin> snsLoginList;

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
                .is_auth((short) 1)
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
        if(selectUser != null) throw new NonCriticalException(ErrorMessage.ACCOUNT_ALREADY_EXIST);

        // 해당 닉네임이 이미 존재한다면 예외처리
        if(userMapper.checkNickname(user.getNickname()) >= 1) throw new NonCriticalException(ErrorMessage.NICKNAME_ALREADY_EXIST);

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

    private Long getLoginUserIdFromJwt(TokenType tokenType){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

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
