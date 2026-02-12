package kz.che.xm.crypto.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.che.xm.crypto.dto.exception.CryptoInvestmentException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static java.lang.String.valueOf;
import static java.util.List.of;
import static kz.che.xm.crypto.dto.exception.CryptoInvestmentError.TOO_MANY_REQUESTS;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class IpRateLimitFilter extends OncePerRequestFilter {
    private static final String FORWARDED_HEADER = "X-Forwarded-For";
    private static final long REQUEST_LIMIT = 5;
    private static final long PER_SECOND = 60;
    private static final DefaultRedisScript<Long> SCRIPT = new DefaultRedisScript<>(
            """
                    local current = redis.call('INCR', KEYS[1])
                    if current == 1 then
                      redis.call('EXPIRE', KEYS[1], ARGV[1])
                    end
                    return current
                    """,
            Long.class
    );

    private final StringRedisTemplate redis;


    public IpRateLimitFilter(@Qualifier("ipFilterRedisTemplate") StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String ip = extractClientIp(request);
        String path = request.getRequestURI();
        if (!path.startsWith("/api/")) {
            chain.doFilter(request, response);
            return;
        }
        String key = "rl:ip:" + ip;
        Long current = redis.execute(SCRIPT, of(key), valueOf(PER_SECOND));
        if (current > REQUEST_LIMIT) {
            throw new CryptoInvestmentException(TOO_MANY_REQUESTS,
                    "To many requests from ip: " + ip + ". Please try after " + PER_SECOND + " seconds.");
        }
        chain.doFilter(request, response);
    }

    private String extractClientIp(HttpServletRequest request) {
        String header = request.getHeader(FORWARDED_HEADER);
        if (!isBlank(header)) {
            return header.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
