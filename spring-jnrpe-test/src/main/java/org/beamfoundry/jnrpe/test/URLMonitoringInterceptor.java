package org.beamfoundry.jnrpe.test;

import it.jnrpe.ICommandLine;
import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.plugins.PluginBase;
import it.jnrpe.plugins.annotations.Option;
import it.jnrpe.plugins.annotations.Plugin;
import it.jnrpe.plugins.annotations.PluginOptions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Plugin(name = "URL", description = "Check a URL")
@PluginOptions({
    @Option(shortName = "u", longName = "url", description = "the URL to check", required = true, hasArgs = true, argName = "text", optionalArgs = false, option = "text")  })
public class URLMonitoringInterceptor extends PluginBase implements HandlerInterceptor {
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private Map <String,JNRPEURLMonitorRecord> urlMap = new ConcurrentHashMap<String,JNRPEURLMonitorRecord>();
	
	public final ReturnValue execute(final ICommandLine cl) {
		String url = cl.getOptionValue("url");
		if (urlMap.containsKey(cl.getOptionValue("url"))) {
			return new ReturnValue(Status.OK, "URL : " + url + " " + urlMap.get(url).getCount());
		}else{
			urlMap.put(url, new JNRPEURLMonitorRecord());
			return new ReturnValue(Status.UNKNOWN, "URL : " + cl.getOptionValue("url") + " registered");
		}
	}

	@Override
	protected String getPluginName() {
		return "CHECK_URL";
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		String url = request.getRequestURI();
		if (urlMap.containsKey(url)) {
			urlMap.get(url).incrementMethodCounter(request.getMethod());
		}
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler, ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		return true;
	}

}
