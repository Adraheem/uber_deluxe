package africa.semicolon.uberdeluxe.config.security.filters;


import africa.semicolon.uberdeluxe.config.security.util.JwtUtil;
import africa.semicolon.uberdeluxe.data.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
public class UberDeluxeAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String authHeader = request.getHeader(AUTHORIZATION);
        if (request.getServletPath().equals("/api/v1/login")||
        request.getServletPath().equals("/api/v1/driver/register") ||
        request.getServletPath().equals("/swagger-ui/index.html#/")
        ){
            filterChain.doFilter(request, response);
        }else{
            if (authHeader!=null&&authHeader.startsWith("Bearer ")){
                String token = request.getHeader(HttpHeaders.AUTHORIZATION);
                String jwt = token.substring("Bearer ".length());
                var res = Jwts.parser().setSigningKey(jwtUtil.getJwtSecret()).isSigned(jwt);
                if (res){
                    List<String> roles = new ArrayList<>();
                    var jwtMap= Jwts.parser().setSigningKey(jwtUtil.getJwtSecret()).parseClaimsJws(jwt);
                    jwtMap.getBody().forEach((k, v)->roles.add(v.toString()));
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(null, null,
                            roles.stream().map(SimpleGrantedAuthority::new).toList());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                }else{
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    mapper.writeValue(response.getOutputStream(), ApiResponse.builder().message("auth failed").build());
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
