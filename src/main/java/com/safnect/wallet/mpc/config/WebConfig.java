package com.safnect.wallet.mpc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.safnect.wallet.mpc.web.AccessInterceptor;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	AccessInterceptor accessInterceptor;
	
	@Value("${app.prod}")
	Boolean prod;
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	if (this.prod) {
            registry.addInterceptor(accessInterceptor)
	            .addPathPatterns("/**") // 拦截所有路径
	            .excludePathPatterns("/runes/rune-utxo/get") // 排除特定路径
            	.excludePathPatterns("/fetch-data/abi-get"); // 排除特定路径
    	}
    }
}
