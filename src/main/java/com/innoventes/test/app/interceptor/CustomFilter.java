package com.innoventes.test.app.interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

@WebFilter("/*")
public class CustomFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            CustomRequestWrapper requestWrapper = new CustomRequestWrapper((HttpServletRequest) request);
            chain.doFilter(requestWrapper, response);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private static class CustomRequestWrapper extends HttpServletRequestWrapper {
        private final byte[] payload;

        public CustomRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);
            String payload = getRequestBody(request);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(payload);
            JsonNode webSiteURLNode = rootNode.get("webSiteURL");

            if (webSiteURLNode != null && !isValidURL(webSiteURLNode.asText())) {
                ((ObjectNode) rootNode).put("webSiteURL", (String) null);
            }
            this.payload = rootNode.toString().getBytes();
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new CustomServletInputStream(payload);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream()));
        }

        private String getRequestBody(HttpServletRequest request) throws IOException {
            try (BufferedReader reader = request.getReader()) {
                StringBuilder payload = new StringBuilder();
                char[] buffer = new char[1024];
                int bytesRead;
                while ((bytesRead = reader.read(buffer)) != -1) {
                    payload.append(buffer, 0, bytesRead);
                }
                return payload.toString();
            }
        }

        private boolean isValidURL(String url) {
            return url != null && (url.startsWith("http://") || url.startsWith("https://"));
        }
    }

    private static class CustomServletInputStream extends javax.servlet.ServletInputStream {
        private final ByteArrayInputStream stream;

        public CustomServletInputStream(byte[] bytes) {
            this.stream = new ByteArrayInputStream(bytes);
        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }

    }
}
