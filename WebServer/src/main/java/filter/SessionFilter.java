package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebFilter(urlPatterns = {"/api/*", "/login", "/logout", "/registration"})
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {

        Cookie[] cookies = ((HttpServletRequest) request).getCookies();

        if(cookies == null){
            cookies = new Cookie[0];
        }

        List<Cookie> cookieList = Arrays.asList(cookies);

        if (cookieList.stream().noneMatch(cookie -> cookie.getName().equals("ET_SESSION"))) {
            Cookie newCookie = new Cookie(
                    "ET_SESSION",
                    ((HttpServletRequest) request).getSession().getId());

            newCookie.setMaxAge(60 * 60 * 24 * 7);
            newCookie.setPath("/");
            ((HttpServletResponse) response).addCookie(newCookie);
        }

        filterChain.doFilter(request, response);
    }


    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
