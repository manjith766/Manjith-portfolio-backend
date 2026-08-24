package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.VisitorLogResponseDTO;
import com.manjith.portfolio.entity.VisitorLog;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class VisitorLogMapperImpl implements VisitorLogMapper {

    @Override
    public VisitorLogResponseDTO toResponseDTO(VisitorLog visitorLog) {
        if ( visitorLog == null ) {
            return null;
        }

        VisitorLogResponseDTO.VisitorLogResponseDTOBuilder visitorLogResponseDTO = VisitorLogResponseDTO.builder();

        visitorLogResponseDTO.id( visitorLog.getId() );
        visitorLogResponseDTO.ipAddress( visitorLog.getIpAddress() );
        visitorLogResponseDTO.userAgent( visitorLog.getUserAgent() );
        visitorLogResponseDTO.pagePath( visitorLog.getPagePath() );
        visitorLogResponseDTO.referrer( visitorLog.getReferrer() );
        visitorLogResponseDTO.country( visitorLog.getCountry() );
        visitorLogResponseDTO.city( visitorLog.getCity() );
        visitorLogResponseDTO.visitedAt( visitorLog.getVisitedAt() );

        return visitorLogResponseDTO.build();
    }
}
