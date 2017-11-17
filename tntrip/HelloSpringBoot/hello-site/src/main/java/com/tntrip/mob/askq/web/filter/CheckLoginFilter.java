package com.tntrip.mob.askq.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by nuc on 2016/11/12.
 */
public class CheckLoginFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckLoginFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        LOGGER.info("Executing servlet filter......");
        filterChain.doFilter(request, response);
    }
}
