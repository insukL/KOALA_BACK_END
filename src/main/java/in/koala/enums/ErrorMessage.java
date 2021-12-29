package in.koala.enums;

public enum ErrorMessage {
	EXCEPTION_FOR_TEST(-1,"TEST를 위한 에러 메시지입니다."),
	UNDEFINED_EXCEPTION(0,"정의되지 않은 에러입니다."),
	NULL_POINTER_EXCEPTION(1,"NULL 여부를 확인해주세요"),
	JSON_PARSE_EXCEPTION(2,"JSON Parse 과정에 문제가 있습니다. 데이터를 확인해주세요"),
	AOP_XSS_SETTER_NO_EXSISTS_EXCEPTION(3,"해당 필드에 SETTER가 존재하지 않습니다."),
	AOP_XSS_FIELD_NO_EXSISTS_EXCEPTION(3,"해당 필드에 FIELD가 존재하지 않습니다."),
	VALIDATION_FAIL_EXCEPTION(4, "입력값이 valid 하지 않습니다"),
	/*
	 * DB_CONSTRAIN INVALID
	 */
	DB_CONSTRAINT_INVALID(1080,"데이터베이스의 고유 제약 조건을 위반하였습니다."),
	
	/*
	 * @VALID INVALID
	 */
	VALID_ANNOTATION_INVALID(1100,"@Validation 에러가 발생하였습니다."),
	PAGENATION_INVALID(1120,"pagenation의 범위를 확인해주세요. 0 < page , 0 < per_count"),
	

	/*
	 * USER
	 */
	DUPLICATED_ACCOUNT_EXCEPTION(123, "이미 존재하는 아이디입니다"),
	DUPLICATED_NICKNAME_EXCEPTION(124, "이미 존재하는 닉네임입니다"),
	DUPLICATED_EMAIL_EXCEPTION(125, "이미 존재하는 이메일입니다"),
	NAVER_LOGIN_ERROR(125, "네이버 로그인 오류"),
	ACCOUNT_NOT_EXIST(126, "존재하지 않는 아이디입니다."),
	WRONG_PASSWORD_EXCEPTION(127, "틀린 비밀번호입니다."),
	USER_NOT_EXIST(128, "존재하지 않는 계정입니다"),
	JWT_NOT_START_BEARER(127, "jwt 가 Bearer 로 시작하지 않습니다"),
	JWT_NOT_EXIST(128, "jwt 이 존재하지 않습니다"),
	SNSTYPE_NOT_VALID(129, "적절하지 않은 sns 타입입니다"),
	ACCESSTOKEN_EXPIRED_EXCEPTION(130, "access token 만료"),
	REFRESHTOKEN_EXPIRED_EXCEPTION(131, "refresh token 만료"),
	ACCESSTOKEN_INVALID_EXCEPTION(132, "유효하지 않은 access token"),
	REFRESHTOKEN_INVALID_EXCEPTION(133, "유효하지 않은 refresh token"),
	PROFILE_SCOPE_ERROR(134, "oauth2 sns 로부터 제공되는 정보가 부족합니다"),
	GOOGLE_LOGIN_ERROR(135, "구글 로그인 오류"),
	KAKAO_LOGIN_ERROR(136, "카카오 로그인 오류"),
	EMAIL_SEND_EXCEED_EXCEPTION(137, "이메인 전송 가능 횟수 초과"),
	UNEXPECTED_EMAIL_CERTIFICATE_ERROR(138, "이메일 인증 중 예상치 못한 오류 발생"),
	EMAIL_EXPIRED_AUTH_EXCEPTION(139, "이메일 인증 유효 기간 만료되었습니다."),
	EMAIL_SECRET_NOT_MATCH(140, "secret 이 일치하지 않습니다"),
	EMAIL_ALREADY_CERTIFICATE(141, "이미 이메일 인증을 완료하였습니다"),
	USER_TYPE_NOT_VALID_EXCEPTION(142, "sns 로 가입하신 경우 비밀번호 찾기 불가합니다"),
	EMAIL_NOT_MATCH(143, "가입할 때 설정한 찾기용 이메일과 일치하지 않습니다."),
	EMAIL_AUTHORIZE_ORDER_EXCEPTION(144, "먼저 이메일을 전송해 주세요"),
	SAME_PASSWORD_EXCEPTION(145, "변경하고자 하는 비밀번호와 기존 비밀번호가 같습니다."),
	EMAIL_NOT_AUTHORIZE_EXCEPTION(146, "이메일 인증이 완료되지 않았습니다"),
	EMAIL_SEND_FAILED(147, "ses 이메일 전송 실패"),
	SNS_TOKEN_NOT_EXIST(148, "sns 로그인용 token 이 존재하지 않습니다"),
	IDENTITY_TOKEN_EXPIRED_EXCEPTION(149, "identity token 유효 기간 만료되었습니다"),
	IDENTITY_TOKEN_INVALID_EXCEPTION(150, "유효하지 않은 identity token 입니다"),
	APPLE_PUBLIC_KEY_EXCEPTION(151, "apple server 에서 public key 가져오는 것 실패했습니다"),


	/*
	 * Scrap, Memo
	 */
	BOARD_NOT_EXIST(250, "글이 존재하지 않습니다"),
	ALREADY_SCRAP_BOARD(251, "이미 보관함에 존재합니다"),
	SCRAP_NOT_EXIST(252, "보관함에 존재하지 않습니다."),
	ALREADY_MEMO_EXIST(253, "이미 메모가 존재합니다."),
	MEMO_NOT_EXIST(254, "메모가 존재하지 않습니다."),


	/**
	 * Crawling
	 */
	UNABLE_CONNECT_TO_PORTAL(300, "아우누리 웹 사이트에 접속할 수 없습니다."),
	UNABLE_CONNECT_TO_DORM(301, "아우미르 웹 사이트에 접속할 수 없습니다."),
	UNABLE_CONNECT_TO_YOUTUBE(302, "유튜브 API 호출이 불가 합니다."),
	UNABLE_CONNECT_TO_FACEBOOK(303, "페이스북 API 호출이 불가 합니다."),
	UNABLE_CONNECT_TO_INSTAGRAM(304, "인스타그램 API 호출이 불가 합니다."),

	/**
	 * FCM Push Notification
	 */
	FAILED_TO_SEND_NOTIFICATION(400, "알림 발송에 실패하였습니다."),

	/**
	 * Keyword
	 */
	DUPLICATED_KEYWORD_EXCEPTION(500, "이미 등록하신 키워드입니다.")
	;



	Integer code;
	String errorMessage;
	ErrorMessage(int code, String errorMessage) {
		this.code = code;
		this.errorMessage = errorMessage;
	}
	
	
	public Integer getCode() {
		return code;
	}
	public String getErrorMessage() {
		return errorMessage;
	}

	
}
