package org.example.setup.filters;

import static org.example.api.Constants.TRACE_ID_KEY;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;

public class MDCRequestIdFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    Filter.super.init(filterConfig);
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {
    try {
      MDC.put(TRACE_ID_KEY, UUID.randomUUID().toString());
      filterChain.doFilter(servletRequest, servletResponse);
    } finally {
      MDC.remove(TRACE_ID_KEY);
    }
  }

}