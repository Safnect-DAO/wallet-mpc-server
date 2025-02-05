package com.safnect.wallet.mpc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.safnect.wallet.mpc.web.AccessInterceptor;
import com.safnect.wallet.mpc.web.TokenAuthInterceptor;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	AccessInterceptor accessInterceptor;
	
	@Autowired
	TokenAuthInterceptor tokenAuthInterceptor;
	
	@Value("${app.prod}")
	Boolean prod;
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	if (this.prod) {
            registry.addInterceptor(accessInterceptor)
	            .addPathPatterns("/**");
            
            registry.addInterceptor(tokenAuthInterceptor)
	            .addPathPatterns("/**") // 拦截所有路径
	            .excludePathPatterns("/runes/rune-utxo/get")
	        	.excludePathPatterns("/fetch-data/**")
	        	.excludePathPatterns("/bca/**");
    	}
    }
}
