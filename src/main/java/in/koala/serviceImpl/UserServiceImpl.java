package in.koala.serviceImpl;

import in.koala.domain.AuthEmail;
import in.koala.domain.DeviceToken;
import in.koala.domain.JWToken;
import in.koala.domain.user.NonUser;
import in.koala.domain.user.NormalUser;
import in.koala.domain.user.User;
import in.koala.enums.*;
import in.koala.exception.CriticalException;
import in.koala.exception.NonCriticalException;
import in.koala.mapper.AuthEmailMapper;
import in.koala.mapper.UserMapper;
import in.koala.service.DeviceTokenService;
import in.koala.service.sns.SnsLogin;
import in.koala.service.UserService;
import in.koala.util.ImageUtil;
import in.koala.util.JwtUtil;
import in.koala.util.S3Util;
import in.koala.util.SesSender;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
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

    private final DeviceTokenService deviceTokenService;
    private final UserMapper userMapper;
    private final AuthEmailMapper authEmailMapper;
    private final HttpServletResponse response;
    private final JwtUtil jwt;
    // list 형식으로 주입받게 되면 해당 인터페이스를 구현하는 모든 클래스를 주입받을 수 있다.
    private final List<SnsLogin> snsLoginList;
    private final SesSender sesSender;
    private final SpringTemplateEngine springTemplateEngine;
    private final S3Util s3Util;
    private final ImageUtil imageUtil;

    @Value("${s3.default_image.url}")
    private String defaultUrl;

    @Override
    public String test() {
        return userMapper.test();
    }

    // 클라이언트 필요없는 서버 개발용
    @Override
    public JWToken snsLogin(String code, SnsType snsType) throws Exception {
        SnsLogin snsLogin = this.initSnsService(snsType);

        Map<String, String> userProfile = snsLogin.requestUserProfile(code);

        // 받은 정보를 이용하여 User domain 생성
        NormalUser snsUser = NormalUser.builder()
                .account(userProfile.get("account"))
                .sns_email(userProfile.get("sns_email"))
                .profile(userProfile.get("profile"))
                .nickname(userProfile.get("nickname"))
                .sns_type(snsType)
                .user_type(UserType.NORMAL)
                .build();

        if(snsUser.getProfile() == null) snsUser.setProfile(defaultUrl);

        Long id = userMapper.getIdByAccount(snsUser.getAccount());

        // 해당 유저가 처음 sns 로그인을 요청한다면 회원가입
        if(id == null) {
            this.snsSingUp(snsUser);
            id = snsUser.getId();
        }

        return this.generateAccessAndRefreshToken(id, UserType.NORMAL);
    }

    @Override
    public JWToken snsSingIn(SnsType snsType, String deviceToken) {
        // sns 별 인터페이스 구현체 변경
        SnsLogin snsLogin = this.initSnsService(snsType);

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String snsToken = request.getHeader("Authorization");

        if(snsToken == null){
            throw new NonCriticalException(ErrorMessage.SNS_TOKEN_NOT_EXIST);
        }

        // snsLogin 에 유저 정보요청
        Map<String, String> userProfile = snsLogin.requestUserProfileBySnsToken(snsToken);

        // 받은 정보를 이용하여 User domain 생성
        NormalUser user = NormalUser.builder()
                .account(userProfile.get("account"))
                .sns_email(userProfile.get("sns_email"))
                .profile(userProfile.get("profile"))
                .nickname(userProfile.get("nickname"))
                .sns_type(snsType)
                .user_type(UserType.NORMAL)
                .build();

        if(user.getProfile() == null) user.setProfile(defaultUrl);

        Long id = userMapper.getIdByAccount(user.getAccount());

        // 해당 유저가 처음 sns 로그인을 요청한다면 회원가입
        if(id == null) {
            this.snsSingUp(user);
            id = user.getId();
        }

        // 디바이스 토큰의 user id 갱신
        if(!checkIsWebUser(deviceToken)) {
            this.setUserIdInDeviceToken(DeviceToken.ofNormalUser(id, deviceToken));
        }

        return generateAccessAndRefreshToken(id, UserType.NORMAL);
    }

    /**
     * 비회원 로그인 메소드
     * 디바이스 토큰을 이용하여 이전에 비회원 로그인 여부를 파악한다
     */
    @Override
    public JWToken nonMemberLogin(String deviceToken) {

        if(checkIsWebUser(deviceToken)){
            throw new NonCriticalException(ErrorMessage.WEB_NOT_SUPPORT);
        }

        DeviceToken token = null;

        if (deviceTokenService.checkTokenExist(deviceToken)) {
            token = deviceTokenService.getDeviceTokenInfoByDeviceToken(deviceToken);
        }

        if(token == null){
            // 비회원 유저가 존재하지 않고
            // device token 또한 존재하지 않는 경우

            NonUser user = NonUser.builder()
                    .user_type(UserType.NON).build();

            this.nonUserSingUp(user);

            token = DeviceToken.ofNonUser(user.getId(), user.getId(), deviceToken);

        } else if(token.getNon_user_id() == null) {
            // DB 의 토큰 테이블의 non user 가 null 인 경우
            // 토큰은 있지만 연결된 비회원 유저가 존재하지 않는다

            NonUser user = NonUser.builder()
                    .user_type(UserType.NON).build();

            this.nonUserSingUp(user);

            token.setNon_user_id(user.getId());
            token.setUser_id(user.getId());
            deviceTokenService.updateTokenTableNonUserId(token);

        } else if(token.getNon_user_id() != null) {
            // 토큰이 존재하고 비회원 유저도 존재하는 경우

            token.setUser_id(token.getNon_user_id());
        }

        // 토큰이 없다면 토큰 생성, 있다면 토큰의 user_id 갱신
        this.setUserIdInDeviceToken(token);

        return generateAccessAndRefreshToken(token.getUser_id(), UserType.NON);
    }

    // sns 별 oauth2 로그인 요청을 하는 메서드, 해당 api 요청한 페이지를 redirect 시킨다.
    // swagger 에서는 작동하지 않는다. 앱에서 작동여부도 확인해봐야 함
    @Override
    public void requestSnsLogin(SnsType snsType) throws Exception {
        SnsLogin snsLogin = initSnsService(snsType);

        //System.out.println(uri);
        response.sendRedirect(snsLogin.getRedirectUri());
    }

    @Override
    public Boolean checkFindEmail(String email) {
        if(!userMapper.getUserByFindEmail(email).isPresent()) {
            return true;

        } else{
            throw new NonCriticalException(ErrorMessage.DUPLICATED_EMAIL_EXCEPTION);
        }
    }

    @Override
    public User signUp(NormalUser user) {
        userMapper.getUserByAccount(user.getAccount())
                .orElseThrow(()->new NonCriticalException(ErrorMessage.DUPLICATED_ACCOUNT_EXCEPTION));

        checkNickname(user.getNickname());
        checkFindEmail(user.getFind_email());
        // 비밀번호 단방향 암호화
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setProfile(defaultUrl);
        user.setUser_type(UserType.NORMAL);
        user.setSns_type(SnsType.NORMAL);

        normalUserSingUp(user);
        return userMapper.getNormalUserById(user.getId()).get();
    }

    @Override
    public JWToken login(NormalUser user, String deviceToken) {
        NormalUser loginUser = userMapper.getUserPassword(user.getAccount())
                .orElseThrow(()->new NonCriticalException(ErrorMessage.ACCOUNT_NOT_EXIST));

        if(!BCrypt.checkpw(user.getPassword(), loginUser.getPassword()))
            throw new NonCriticalException(ErrorMessage.WRONG_PASSWORD_EXCEPTION);

        if(!checkIsWebUser(deviceToken)) {
            setUserIdInDeviceToken(DeviceToken.ofNormalUser(loginUser.getId(), deviceToken));
        }

        return generateAccessAndRefreshToken(loginUser.getId(), UserType.NORMAL);
    }

    @Override
    public User getLoginUserInfo() {
        return getUserInfo(getLoginUserIdFromJwt(TokenType.ACCESS));
    }

    @Override
    public NormalUser getLoginNormalUserInfo(){
        Long id = this.getLoginUserIdFromJwt(TokenType.ACCESS);

        return userMapper.getNormalUserById(id)
                .orElseThrow(()->new NonCriticalException(ErrorMessage.USER_NOT_EXIST));
    }

    @Override
    public void updateNickname(String nickname) {
        if (userMapper.checkNickname(nickname) > 0) {
            throw new NonCriticalException(ErrorMessage.DUPLICATED_NICKNAME_EXCEPTION);
        }

        NormalUser user = this.getLoginNormalUserInfo();
        user.setNickname(nickname);

        userMapper.updateNickname(user);
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
    public JWToken refresh() {
        User user = getUserInfo(getLoginUserIdFromJwt(TokenType.REFRESH));

        return generateAccessAndRefreshToken(user.getId(), user.getUser_type());
    }

    // 별개의 서비스로 분리 요망
    @Override
    public void sendEmail(AuthEmail authEmail, EmailType emailType) {

        if(emailType.equals(EmailType.UNIVERSITY)){
            String[] strings = authEmail.getEmail().split("@");
            if(!strings[1].equals("koreatech.ac.kr")){
                throw new NonCriticalException(ErrorMessage.EMAIL_NOT_UNIVERSITY);
            }
        }

        NormalUser user = initNormalUserByEmailType(authEmail, emailType);
        System.out.println(user);
        // sns 로그인으로 가입한 계정이 비밀번호 찾기 혹은 계정 찾기를 요청할 경우 발생하는 예외
        if(user.getSns_type() != SnsType.NORMAL && (emailType.equals(EmailType.ACCOUNT) || emailType.equals(EmailType.PASSWORD))){
            throw new CriticalException(ErrorMessage.USER_TYPE_NOT_VALID_EXCEPTION);
        }

        // 비밀번호 찾기의 경우 가입할 때 설정한 이메일과 일치하는 지 확인
        if(emailType.equals(EmailType.PASSWORD) && !user.getFind_email().equals(authEmail.getEmail())){
            throw new NonCriticalException(ErrorMessage.EMAIL_NOT_MATCH);
        }

        // 이미 이메일 인증을 끝낸 계정이 학교 인증 이메일 전송을 요청하면 예외 발생
        if(emailType.equals(EmailType.UNIVERSITY) && checkUserUniversityCertification(user)){
            throw new NonCriticalException(ErrorMessage.USER_ALREADY_CERTIFICATE);
        }

        authEmail.setUser_id(user.getId());

        // 10분에 최대 5개 전송가능
        if(isEmailSentNumExceed(authEmail)){
            throw new NonCriticalException(ErrorMessage.EMAIL_SEND_EXCEED_EXCEPTION);
        }

        StringBuilder secret = new StringBuilder();
        Random random = new Random();

        // 난수 생성
        for(int i = 0; i < 5; i++){
            secret.append(random.nextInt(10));
        }

        Context context = new Context();
        context.setVariable("secret", secret.toString());

        String body = springTemplateEngine.process("authenticationEmail", context);

        try {
            sesSender.sendMail("no-reply@koala.im", authEmail.getEmail(),
                    "KOALA 서비스 인증 메일입니다.", body);

        } catch(Exception e){
            throw new NonCriticalException(ErrorMessage.EMAIL_SEND_FAILED);
        }

        // 이메일 유효 기간 설정
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Timestamp(System.currentTimeMillis()));
        calendar.add(Calendar.SECOND, 5 * 60);
        authEmail.setExpired_at(new Timestamp(calendar.getTimeInMillis()));

        authEmail.setSecret(secret.toString());
        authEmail.setType(emailType);

        // 이전에 보냈던 이메일들은 전부 무효화
        authEmailMapper.expirePastAuthEmail(authEmail);

        // 이번에 보낸 이메일 삽입
        authEmailMapper.insertAuthEmail(authEmail);
    }
    
    @Override
    public void certificateEmail(AuthEmail authEmail, EmailType emailType) {
        // email 전송 종류에 따른 유저 초기화
        User user = initNormalUserByEmailType(authEmail, emailType);

        authEmail.setUser_id(user.getId());
        authEmail.setType(emailType);

        List<AuthEmail> authEmailList = authEmailMapper.getUndeletedAuthEmailByUserIdAndType(authEmail);

        // 만약 delete 되지 않은 이메일이 하나보다 많다면 예외 발생, 발생하면 논리 오류
        if(authEmailList.size() > 1){
            throw new CriticalException(ErrorMessage.UNEXPECTED_EMAIL_CERTIFICATE_ERROR);
        }

        // 이메일 전송이 선행되어야 함
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

        // 만약 학교 인증이라면 인증했다는 사실을 User 에 기록
        if(emailType.equals(EmailType.UNIVERSITY)){
            userMapper.updateIsAuth(selectedAuthEmail.getUser_id());
            // 학교 인증 메일 만료
            authEmailMapper.expirePastAuthEmail(authEmail);
        }

        // 인증 완료하였으니, 해당 메일 인증 성공했다고 auth_email 테이블에 체크
        authEmailMapper.setIsAuth(selectedAuthEmail.getId());
    }

    @Override
    public boolean isUniversityCertification() {
        NormalUser user = getLoginNormalUserInfo();

        if(checkUserUniversityCertification(user)){
            return true;

        } else {
            throw new NonCriticalException(ErrorMessage.EMAIL_NOT_AUTHORIZE_EXCEPTION);
        }
    }

    private boolean checkUserUniversityCertification(NormalUser user) {
        return user.getIs_auth() == 1;
    }

    @Override
    public Boolean checkAccount(String account) {
        if(userMapper.getUserByAccount(account).isPresent()){
            return true;

        } else{
            throw new NonCriticalException(ErrorMessage.ACCOUNT_NOT_EXIST);
        }
    }

    @Override
    public void changePassword(NormalUser user) {
        NormalUser selectedUser = userMapper.getUserPassword(user.getAccount())
                .orElseThrow(()->new NonCriticalException(ErrorMessage.ACCOUNT_NOT_EXIST));

        if(checkEmailAuthPrecede(selectedUser.getId(), EmailType.PASSWORD)){
            throw new NonCriticalException(ErrorMessage.EMAIL_NOT_AUTHORIZE_EXCEPTION);
        }
        
        if(BCrypt.checkpw(user.getPassword(), selectedUser.getPassword())){
            throw new NonCriticalException(ErrorMessage.SAME_PASSWORD_EXCEPTION);
        }

        selectedUser.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        userMapper.updatePassword(selectedUser);

        AuthEmail authEmail = AuthEmail.builder()
                .user_id(selectedUser.getId())
                .type(EmailType.PASSWORD)
                .build();

        authEmailMapper.expirePastAuthEmail(authEmail);
    }

    private boolean checkEmailAuthPrecede(Long userId, EmailType password) {
        return authEmailMapper.getUndeletedIsAuthNumByUserId(userId, password) <= 0;
    }

    @Override
    public String findAccount(String email) {
        NormalUser user = userMapper.getUserByFindEmail(email)
                .orElseThrow(()->new NonCriticalException(ErrorMessage.USER_NOT_EXIST));

        if(checkEmailAuthPrecede(user.getId(), EmailType.ACCOUNT)){
            throw new NonCriticalException(ErrorMessage.EMAIL_NOT_AUTHORIZE_EXCEPTION);
        }

         AuthEmail authEmail = AuthEmail.builder()
                .user_id(user.getId())
                .type(EmailType.ACCOUNT)
                .build();

        authEmailMapper.expirePastAuthEmail(authEmail);

        String account = user.getAccount();
        account = account.substring(0, account.length() - 2);
        account += "**";

        return account;
    }

    @Override
    public void softDeleteUser() {
        NormalUser user = this.getLoginNormalUserInfo();

        String deleted = "deletedUser_" + user.getId().toString();

        user.setNickname(deleted);
        user.setAccount(deleted);
        user.setFind_email(deleted + "@deletedUser.deleted");
        user.setSns_email(deleted + "@deletedUser.deleted");

        userMapper.softDeleteNormalUser(user);
        userMapper.softDeleteUser(user);
    }

    @Override
    public String editProfile(MultipartFile multipartFile){

        multipartFile = imageUtil.resizing(multipartFile, 500);
        NormalUser selectedUser = this.getLoginNormalUserInfo();

        String profileUrl = selectedUser.getProfile();

        if(!profileUrl.equals(defaultUrl)){
            s3Util.deleteFile(profileUrl);
        }

        profileUrl = s3Util.uploader(multipartFile);
        userMapper.updateUserProfile(profileUrl, selectedUser.getId());

        return profileUrl;
    }


    private boolean isEmailSentNumExceed(AuthEmail authEmail){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, -601);
        Timestamp start = new Timestamp(calendar.getTimeInMillis());

        // 최근 10분안에 전송한 이메일 개수확인
        return authEmailMapper.getAuthEmailNumByUserIdAndType(authEmail.getUser_id(), authEmail.getType(), start) >= 5;
    }

    private Long getLoginUserIdFromJwt(TokenType tokenType){
        return Long.valueOf(String.valueOf(getClaimsFromJwt(tokenType).get("id")));
    }

    private Claims getClaimsFromJwt(TokenType tokenType){
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("Authorization");

        if(token == null){
            throw new NonCriticalException(ErrorMessage.ACCESS_TOKEN_NOT_EXIST);
        }

        return jwt.getClaimsFromJwt(token, tokenType);
    }

    private JWToken generateAccessAndRefreshToken(Long id, UserType userType){
        return JWToken.ofLogin(jwt.generateToken(id, TokenType.ACCESS, userType)
                , jwt.generateToken(id, TokenType.REFRESH, userType));
    }

    // 로그인 요청이 들어온 sns 에 맞춰 SnsLogin 인터페이스 구현체 결정
    private SnsLogin initSnsService(SnsType snsType){

        for(SnsLogin snsLogin : snsLoginList){
            if(snsLogin.getSnsType().equals(snsType)) return snsLogin;
        }

        throw new NonCriticalException(ErrorMessage.SNSTYPE_NOT_VALID);
    }


    private NormalUser initNormalUserByEmailType(AuthEmail authEmail, EmailType emailType) {

        if(emailType.equals(EmailType.ACCOUNT)){
            return userMapper.getUserByFindEmail(authEmail.getEmail())
                    .orElseThrow(()->new NonCriticalException(ErrorMessage.USER_NOT_EXIST));

        } else if(emailType.equals(EmailType.UNIVERSITY)){
            return getLoginNormalUserInfo();

        } else {
            return userMapper.getUserByAccount(authEmail.getAccount())
                    .orElseThrow(()->new NonCriticalException(ErrorMessage.USER_NOT_EXIST));
        }
    }

    private void snsSingUp(NormalUser user){
        userMapper.insertUser(user);
        userMapper.snsSignUp(user);
        user.setNickname("TEMP_NICKNAME_" + user.getId().toString());
        userMapper.updateNickname(user);
    }

    private void normalUserSingUp(NormalUser user){
        userMapper.insertUser(user);
        userMapper.signUp(user);
    }

    // 소켓 연결시 사용할 JWT 토큰 발급
    @Override
    public JWToken getSocketToken(){
        NormalUser user = this.getLoginNormalUserInfo();

        if(!checkUserUniversityCertification(user)){
            throw new NonCriticalException(ErrorMessage.USER_NOT_AUTH);
        }

        return JWToken.ofChat(jwt.generateToken(user.getId(), TokenType.SOCKET, UserType.NORMAL));
    }
  
    private void nonUserSingUp(NonUser user){
        userMapper.insertUser(user);
        userMapper.insertNonMemberUser(user);
    }

  
    private void setUserIdInDeviceToken(DeviceToken deviceToken){
        if(!deviceTokenService.checkTokenExist(deviceToken.getToken())) {
            deviceTokenService.insertDeviceToken(deviceToken);

        } else {
            deviceTokenService.updateTokenTableUserId(deviceToken);
        }
    }

    // 다형성 구현
    private User getUserInfo(Long id){
        UserType userType = userMapper.getUserType(id);

        if(userType.equals(UserType.NORMAL)) {
            return userMapper.getNormalUserById(id)
                    .orElseThrow(()->new NonCriticalException(ErrorMessage.USER_NOT_EXIST));

        } else {
            return userMapper.getNonUserById(id)
                    .orElseThrow(()->new NonCriticalException(ErrorMessage.USER_NOT_EXIST));
        }
    }

    private boolean checkIsWebUser(String deviceToken){
        return deviceToken.startsWith("webuser");
    }

}
