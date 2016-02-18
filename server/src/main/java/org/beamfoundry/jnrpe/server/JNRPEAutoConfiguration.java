package org.beamfoundry.jnrpe.server;


import it.jnrpe.JNRPE;
import it.jnrpe.JNRPEBuilder;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.IPluginInterface;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.plugins.PluginRepository;
import it.jnrpe.utils.PluginRepositoryUtil;
import it.jnrpe.utils.StringUtils;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JNRPEProperties.class)
@ConditionalOnProperty(prefix = "jnrpe.server", name = "enabled", havingValue = "true", matchIfMissing = true)
public class JNRPEAutoConfiguration {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private JNRPEProperties configuration;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Bean(destroyMethod = "shutdown")
	@ConditionalOnMissingBean
	public JNRPE jNRPEServer() {
		log.debug("JNRPEServer : Initializing ");
		JNRPEBuilder builder = JNRPEBuilder.forRepositories(jNRPEPluginRepository(), jNRPECommandRepository());
		builder.acceptParams(configuration.isAcceptParams());
		builder.withMaxAcceptedConnections(configuration.getMaxConnections());
		builder.withReadTimeout(configuration.getReadTimeout());
		builder.withWriteTimeout(configuration.getWriteTimeout());
		for (Iterator<String> ii = configuration.getAcceptedHosts().iterator(); ii.hasNext();) {
			final String acceptedHost = ii.next();
			log.debug("JNRPEServer : Adding 'acceptedHosts' : "+acceptedHost);
			builder.acceptHost(acceptedHost);
		}
		
		JNRPE jnrpe = builder.build();
		for (Iterator<String> ii = configuration.getBindAddresses().iterator(); ii.hasNext();) {
			
			int iPort = JNRPEProperties.DEFAULT_PORT;
			final String bindAddress = ii.next();
			log.debug("JNRPEServer : Adding 'bindAddresses' : "+bindAddress);
			boolean ssl = bindAddress.toUpperCase().startsWith("SSL/");
			String sAddress = bindAddress;
	        if (ssl) {
	            sAddress = bindAddress.substring("SSL/".length());
	        }
            String[] vsParts = sAddress.split(":");
            String sIp = vsParts[0];
            if (vsParts.length > 1) {
                iPort = Integer.parseInt(vsParts[1]);
            }
            
            try {
                jnrpe.listen(sIp, iPort, ssl);
            } catch (UnknownHostException e) {
                System.out.println(String.format("Error binding the server to %s:%d : %s", sIp, iPort, e.getMessage()));
            }
		}
		
		return jnrpe;
	}
	
	
	@Bean
	@ConditionalOnMissingBean
	public PluginRepository jNRPEPluginRepository() {
		PluginRepository pr = new PluginRepository();
		
		for (Map.Entry <String, IPluginInterface> entry : applicationContext.getBeansOfType(IPluginInterface.class,false,false).entrySet()) {
			log.debug("JNRPEServer : Adding Plugin "+entry.getValue().getClass().getCanonicalName());
			PluginDefinition pdef = PluginRepositoryUtil.loadFromPluginAnnotation(entry.getValue());
			pr.addPluginDefinition(pdef);
		}

		return pr;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public CommandRepository jNRPECommandRepository() {
		CommandRepository cr = new CommandRepository();
		for (Map.Entry<String, String> entry : configuration.getCommands().entrySet()) {
			String sCommandName = entry.getKey();
			
            String[] vElements = StringUtils.split(entry.getValue(), false);
            String sPluginName = vElements[0];

            StringBuilder cmdLine = new StringBuilder();

            for (int i = 1; i < vElements.length; i++) {
                cmdLine.append(quoteAndEscape(vElements[i])).append(' ');
            }
            log.debug("JNRPEServer : Adding Command '"+sCommandName+"' Plugin:'"+sPluginName+"' Arg:'"+cmdLine.toString()+"'");
			CommandDefinition cd = new CommandDefinition(sCommandName, sPluginName);
			cd.setArgs(cmdLine.toString());
			cr.addCommandDefinition(cd);
		}
		return cr;
	}
	
    private String quoteAndEscape(final String string) {
        if (string.indexOf(' ') == -1) {
            return string;
        }

        return "\"" + string.replaceAll("\"", "\\\"") + "\"";
    }
}
