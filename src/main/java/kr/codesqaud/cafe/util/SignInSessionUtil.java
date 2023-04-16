package kr.codesqaud.cafe.util;

import javax.servlet.http.HttpSession;
import kr.codesqaud.cafe.config.session.AccountSession;

public class SignInSessionUtil {

    public static final String SIGN_IN_SESSION_NAME = "account";

    public static void create(HttpSession session, AccountSession accountSession) {
        session.setAttribute(SIGN_IN_SESSION_NAME, accountSession);
        session.setMaxInactiveInterval(1800);
    }

    public static void invalidate(HttpSession session) {
        if (session != null && session.getAttribute(SIGN_IN_SESSION_NAME) != null) {
            session.invalidate();
        }
    }
}
