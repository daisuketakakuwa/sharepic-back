package jp.sharepic.sharepicback.infra.factory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResponseFactory {

    @Autowired
    private ModelMapper mapper;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private void initMapper() {

        mapper.addConverter(new AbstractConverter<LocalDate, String>() {
            protected String convert(LocalDate source) {
                return source.format(DATE_FORMAT);
            }
        });

        mapper.addConverter(new AbstractConverter<LocalDateTime, String>() {
            protected String convert(LocalDateTime source) {
                return source.format(DATE_TIME_FORMAT);
            }
        });
    }

    public <D> D map(Object src, Class<D> dstType) {
        return mapper.map(src, dstType);
    }

    public <D> List<D> map(List<?> srcs, Class<D> dstType) {
        return srcs.stream().map(src -> map(src, dstType)).collect(Collectors.toList());
    }

    public <D> Set<D> map(Set<?> srcs, Class<D> dstType) {
        return srcs.stream().map(src -> map(src, dstType)).collect(Collectors.toSet());
    }

}