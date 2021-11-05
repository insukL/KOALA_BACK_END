package in.koala.enums;

public enum ErrorMessage {
	EXCEPTION_FOR_TEST(-1,"TEST를 위한 에러 메시지입니다."),
	UNDEFINED_EXCEPTION(0,"정의되지 않은 에러입니다."),
	NULL_POINTER_EXCEPTION(1,"NULL 여부를 확인해주세요"),
	JSON_PARSE_EXCEPTION(2,"JSON Parse 과정에 문제가 있습니다. 데이터를 확인해주세요"),
	AOP_XSS_SETTER_NO_EXSISTS_EXCEPTION(3,"해당 필드에 SETTER가 존재하지 않습니다."),
	AOP_XSS_FIELD_NO_EXSISTS_EXCEPTION(3,"해당 필드에 FIELD가 존재하지 않습니다."),
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
	ACCOUNT_ALREADY_EXIST(123, "이미 존재하는 아이디입니다"),
	NICKNAME_ALREADY_EXIST(124, "이미 존재하는 닉네임입니다"),
	NAVER_LOGIN_ERROR(125, "네이버 로그인 오류"),
	ACCOUNT_NOT_EXIST(126, "존재하지 않는 아이디입니다."),
	WRONG_PASSWORD_EXCEPTION(127, "틀린 비밀번호입니다."),
	USER_NOT_EXIST(128, "존재하지 않는 계정입니다"),
	JWT_NOT_START_BEARER(127, "jwt 가 Bearer 로 시작하지 않습니다"),
	JWT_NOT_EXIST(128, "jwt 이 존재하지 않습니다"),
	SNSTYPE_NOT_VALID(129, "적절하지 않은 sns 요청입니다"),
	ACCESSTOKEN_EXPIRED_EXCEPTION(130, "access token 만료"),
	REFRESHTOKEN_EXPIRED_EXCEPTION(131, "refresh token 만료"),
	ACCESSTOKEN_INVALID_EXCEPTION(132, "유효하지 않은 access token"),
	REFRESHTOKEN_INVALID_EXCEPTION(133, "유효하지 않은 refresh token"),


	/*
	 * Scrap, Memo
	 */
	BOARD_NOT_EXIST(200, "글이 존재하지 않습니다"),
	ALREADY_SCRAP_BOARD(201, "이미 보관함에 있습니다")

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
