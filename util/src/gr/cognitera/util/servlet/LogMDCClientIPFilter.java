package gr.cognitera.util.servlet;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.MDC;

import gr.cognitera.util.servlet.ClientIPAddress;

/* This kind of functionality can also be implemented (perhaps in a cleaner way) by means 
   of a ServletRequestListener.

   see: https://stackoverflow.com/a/21137759/274677

*/
public class LogMDCClientIPFilter implements Filter {

  @Override
  public void init(FilterConfig config) throws ServletException {
      //
  }


  @Override
  public void doFilter(ServletRequest req
                       , ServletResponse response
                       , FilterChain chain) throws IOException, ServletException {
    try {
       HttpServletRequest request = (HttpServletRequest) req;
       MDC.put("client-ip", String.format("client-ip: %s", ClientIPAddress.getFrom(request)));
       chain.doFilter(request, response);

    } finally {
       MDC.remove("client-ip");
    }
  }

  @Override
  public void destroy() {
        //
  }

}
