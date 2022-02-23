package in.koala.interceptor;

import in.koala.annotation.Auth;
import in.koala.enums.ErrorMessage;
import in.koala.enums.TokenType;
import in.koala.enums.UserType;
import in.koala.exception.NonCriticalException;
import in.koala.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private final JwtUtil jwt;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) return true;

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Auth auth = handlerMethod.getMethod().getDeclaredAnnotation(Auth.class);

        if (auth == null) {
            return true;

        } else if (auth != null) {

            String accessToken = request.getHeader("Authorization");

            // access token 이 valid 하지 않다면 false
            if (!jwt.validateToken(accessToken, TokenType.ACCESS)) {
                return false;
            }

            UserType userType = UserType.getUserType((String) jwt.getClaimFromJwt(accessToken).get("aud"));

            if (auth.role() == UserType.NON || auth.role() == null) {
                return true;

            } else if (auth.role() == UserType.NORMAL) {
                if (userType == UserType.NORMAL) {
                    return true;
                }
            }
        }

        throw new NonCriticalException(ErrorMessage.FORBIDDEN_EXCEPTION);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }
}
