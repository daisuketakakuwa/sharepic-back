package jp.sharepic.sharepicback;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharepicBackConfiguration {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
