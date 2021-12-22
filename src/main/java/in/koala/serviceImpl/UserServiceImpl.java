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
        SnsLogin snsLogin = this.initSnsService(snsType);

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
        if(id == null) {
            System.out.println(snsUser.getUser_type());
            userMapper.snsSignUp(snsUser);
        }

        return generateAccessAndRefreshToken(id);
    }

    @Override
    public Map<String, String> snsSingIn(SnsType snsType) {
        // sns 별 인터페이스 구현체 변경
        SnsLogin snsLogin = this.initSnsService(snsType);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String accessToken = request.getHeader("Sns-Token");

        if(accessToken == null){
            throw new NonCriticalException(ErrorMessage.SNS_TOKEN_NOT_EXIST);
        }

        // snsLogin 에 유저 정보요청
        Map<String, String> userProfile = snsLogin.requestUserProfileByAccessToken(accessToken);

        // 받은 정보를 이용하여 User domain 생성
        User user = User.builder()
                .account(userProfile.get("account"))
                .sns_email(userProfile.get("sns_email"))
                .profile(userProfile.get("profile"))
                .nickname(userProfile.get("nickname"))
                .user_type((short) 1)
                .build();

        Long id = userMapper.getIdByAccount(user.getAccount());

        // 해당 유저가 처음 sns 로그인을 요청한다면 회원가입
        if(id == null) {
            System.out.println(user.getUser_type());
            userMapper.snsSignUp(user);
        }

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
    public Boolean checkFindEmail(String email) {
        if(userMapper.getUserByFindEmail(email) == null) {
            return true;

        } else{
            throw new NonCriticalException(ErrorMessage.DUPLICATED_EMAIL_EXCEPTION);
        }
    }

    @Override
    public User signUp(User user) {
        User selectUser = userMapper.getUserByAccount(user.getAccount());

        // 해당 계정명이 이미 존재한다면 예외처리
        if(selectUser != null) throw new NonCriticalException(ErrorMessage.DUPLICATED_ACCOUNT_EXCEPTION);

        // 해당 닉네임이 이미 존재한다면 예외처리
        if(userMapper.checkNickname(user.getNickname()) >= 1) throw new NonCriticalException(ErrorMessage.DUPLICATED_NICKNAME_EXCEPTION);

        if(userMapper.getUserByFindEmail(user.getFind_email()) != null) throw new NonCriticalException(ErrorMessage.DUPLICATED_EMAIL_EXCEPTION);
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

    // 각 경우에 따라 분리 예정
    @Override
    public void sendEmail(AuthEmail authEmail) {

        User user = null;

        if(authEmail.getType() == 2){
            user = userMapper.getUserByFindEmail(authEmail.getEmail());

        } else {
            user = userMapper.getUserByAccount(authEmail.getAccount());
        }

        if(user == null){
            throw new NonCriticalException(ErrorMessage.USER_NOT_EXIST);
        }

        // sns 로그인으로 가입한 계정이 비밀번호 찾기 혹은 계정 찾기를 요청할 경우 발생하는 예외
        if(user.getUser_type() == 1 && (authEmail.getType() == 0 || authEmail.getType() == 2)){
            throw new NonCriticalException(ErrorMessage.USER_TYPE_NOT_VALID_EXCEPTION);
        }

        if(authEmail.getType() == 0 && !user.getFind_email().equals(authEmail.getEmail())){
            throw new NonCriticalException(ErrorMessage.EMAIL_NOT_MATCH);
        }

        // 이미 이메일 인증을 끝낸 계정이 채팅 인증 이메일 전송을 요청하면 예외 발생
        if(authEmail.getType() == 1 && user.getIs_auth() == 1){
            throw new NonCriticalException(ErrorMessage.EMAIL_ALREADY_CERTIFICATE);
        }

        authEmail.setUser_id(user.getId());

        // 10분에 최대 5개 전송가능
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

        try {
            sesSender.sendMail("no-reply@koala.im", authEmail.getEmail(),
                    "KOALA 서비스 인증 메일입니다.", body);

        } catch(Exception e){
            throw new NonCriticalException(ErrorMessage.EMAIL_SEND_FAILED);
        }

        // 이전에 보냈던 이메일들은 전부 무효화
        authEmailMapper.expirePastAuthEmail(authEmail);

        // 이메일 유효 기간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Timestamp(System.currentTimeMillis()));
        calendar.add(Calendar.SECOND, 5 * 60);
        authEmail.setExpired_at(new Timestamp(calendar.getTimeInMillis()));

        authEmail.setSecret(secret);

        // 이번에 보낸 이메일 삽입
        authEmailMapper.insertAuthEmail(authEmail);

        return;
    }

    // 각 경우에 따라 분리예정
    @Override
    public void certificateEmail(AuthEmail authEmail) {

        User user = null;

        if(authEmail.getType() == 2){
            user = userMapper.getUserByFindEmail(authEmail.getEmail());

        } else {
            user = userMapper.getUserByAccount(authEmail.getAccount());
        }

        if(user == null){
            throw new NonCriticalException(ErrorMessage.USER_NOT_EXIST);
        }

        authEmail.setUser_id(user.getId());

        List<AuthEmail> authEmailList = authEmailMapper.getUndeletedAuthEmailByUserIdAndType(authEmail);

        // 만약 delete 되지 않은 이메일이 하나보다 많다면 예외 발생, 없겠지만 혹시나...
        if(authEmailList.size() > 1){
            authEmailMapper.expirePastAuthEmail(authEmail);
            throw new NonCriticalException(ErrorMessage.UNEXPECTED_EMAIL_CERTIFICATE_ERROR);
        }

        // 이메일 전송이 전행되어야 함
        if(authEmailList.size() <= 0){
            throw new NonCriticalException(ErrorMessage.EMAIL_AUTHORIZE_ORDER_EXCEPTION);
        }

        AuthEmail selectedAuthEmail = authEmailList.get(0);

        // 메일 인증 유효기간 지났을때 발생
        if(selectedAuthEmail.getExpired_at().before(new Timestamp(System.currentTimeMillis()))){
            throw new NonCriticalException(ErrorMessage.EMAIL_EXPIRED_AUTH_EXCEPTION);
        }

        // secret 이 일치하지 않음
        if(!selectedAuthEmail.getSecret().equals(authEmail.getSecret())){
            throw new NonCriticalException(ErrorMessage.EMAIL_SECRET_NOT_MATCH);
        }

        // 만약 채팅 인증이라면 인증했다는 사실을 User 에 기록
        if(authEmail.getType() == 1){
            userMapper.updateIsAuth(selectedAuthEmail.getUser_id());
        }

        // 인증 완료하였으니 체크
        authEmailMapper.setIsAuth(selectedAuthEmail.getId());

        // 만약 학교 인증이거나 계정 찾기면 만료
        if(authEmail.getType() == 1 || authEmail.getType() == 2) {
            authEmailMapper.expirePastAuthEmail(authEmail);
        }
        return;
    }

    @Override
    public boolean isEmailCertification() {
        User user = getLoginUserInfo();

        if(user.getIs_auth() == 1){
            return true;

        } else {
            throw new NonCriticalException(ErrorMessage.EMAIL_NOT_AUTHORIZE_EXCEPTION);
        }
    }

    @Override
    public Boolean checkAccount(String account) {
        User user = userMapper.getUserByAccount(account);

        if(user == null){
            throw new NonCriticalException(ErrorMessage.ACCOUNT_NOT_EXIST);
        }

        return true;
    }

    @Override
    public void changePassword(User user) {

        User selectedUser = userMapper.getUserPassword(user.getAccount());

        if(selectedUser == null){
            throw new NonCriticalException(ErrorMessage.ACCOUNT_NOT_EXIST);
        }

        if(authEmailMapper.getUndeletedIsAuthNumByUserId(selectedUser.getId(), 0) <= 0){
            throw new NonCriticalException(ErrorMessage.EMAIL_NOT_AUTHORIZE_EXCEPTION);
        }
        
        // 변경하고자 하는 비밀번호와 기존 비밀번호가 같으변 발생하는 예외
        if(BCrypt.checkpw(user.getPassword(), selectedUser.getPassword())){
            throw new NonCriticalException(ErrorMessage.SAME_PASSWORD_EXCEPTION);
        }

        selectedUser.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        userMapper.updatePassword(selectedUser);

        AuthEmail authEmail = new AuthEmail();
        authEmail.setUser_id(selectedUser.getId());
        authEmail.setType((short)0);

        authEmailMapper.expirePastAuthEmail(authEmail);
    }

    @Override
    public String findAccount(String email) {
        User user = userMapper.getUserByFindEmail(email);

        if(user == null){
            throw new NonCriticalException(ErrorMessage.USER_NOT_EXIST);
        }

        if(authEmailMapper.getUndeletedIsAuthNumByUserId(user.getId(), 2) <= 0){
            throw new NonCriticalException(ErrorMessage.EMAIL_NOT_AUTHORIZE_EXCEPTION);
        }

        String account = user.getAccount();
        account = account.substring(0, account.length() - 2);
        account += "**";

        return account;
    }

    @Override
    public void softDeleteUser() {
        User user = this.getLoginUserInfo();

        String deleted = "deletedUser_" + user.getId().toString();

        user.setNickname(deleted);
        user.setAccount(deleted);
        user.setFind_email(deleted + "@deletedUser.deleted");
        user.setSns_email(deleted + "@deletedUser.deleted");

        userMapper.softDeleteUser(user);
    }

    private boolean isEmailSentNumExceed(AuthEmail authEmail){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, -601);
        Timestamp start = new Timestamp(calendar.getTimeInMillis());

        // 최근 10분안에 전송한 이메일 개수확인
        if(authEmailMapper.getAuthEmailNumByUserIdAndType(authEmail.getUser_id(), authEmail.getType(), start) >= 5){
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
