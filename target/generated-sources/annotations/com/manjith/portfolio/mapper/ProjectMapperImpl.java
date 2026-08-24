package com.manjith.portfolio.mapper;

import com.manjith.portfolio.dto.response.ProjectImageResponseDTO;
import com.manjith.portfolio.dto.response.ProjectResponseDTO;
import com.manjith.portfolio.dto.response.ProjectSummaryResponseDTO;
import com.manjith.portfolio.entity.Category;
import com.manjith.portfolio.entity.Project;
import com.manjith.portfolio.entity.ProjectImage;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-22T17:19:48+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Autowired
    private CommonMapper commonMapper;

    @Override
    public ProjectResponseDTO toResponseDTO(Project project) {
        if ( project == null ) {
            return null;
        }

        ProjectResponseDTO.ProjectResponseDTOBuilder projectResponseDTO = ProjectResponseDTO.builder();

        projectResponseDTO.category( commonMapper.toCategorySummaryDTO( project.getCategory() ) );
        projectResponseDTO.images( toImageResponseDTOList( project.getImages() ) );
        projectResponseDTO.skills( commonMapper.toSkillSummaryDTOList( project.getSkills() ) );
        projectResponseDTO.id( project.getId() );
        projectResponseDTO.title( project.getTitle() );
        projectResponseDTO.slug( project.getSlug() );
        projectResponseDTO.description( project.getDescription() );
        projectResponseDTO.shortDescription( project.getShortDescription() );
        projectResponseDTO.githubUrl( project.getGithubUrl() );
        projectResponseDTO.liveDemoUrl( project.getLiveDemoUrl() );
        projectResponseDTO.coverImageUrl( project.getCoverImageUrl() );
        projectResponseDTO.isFeatured( project.getIsFeatured() );
        projectResponseDTO.displayOrder( project.getDisplayOrder() );
        projectResponseDTO.viewCount( project.getViewCount() );
        projectResponseDTO.createdAt( project.getCreatedAt() );
        projectResponseDTO.updatedAt( project.getUpdatedAt() );

        projectResponseDTO.features( mapFeatureTexts(project.getFeatures()) );

        return projectResponseDTO.build();
    }

    @Override
    public ProjectSummaryResponseDTO toSummaryDTO(Project project) {
        if ( project == null ) {
            return null;
        }

        ProjectSummaryResponseDTO.ProjectSummaryResponseDTOBuilder projectSummaryResponseDTO = ProjectSummaryResponseDTO.builder();

        projectSummaryResponseDTO.categoryName( projectCategoryName( project ) );
        projectSummaryResponseDTO.id( project.getId() );
        projectSummaryResponseDTO.title( project.getTitle() );
        projectSummaryResponseDTO.slug( project.getSlug() );
        projectSummaryResponseDTO.shortDescription( project.getShortDescription() );
        projectSummaryResponseDTO.coverImageUrl( project.getCoverImageUrl() );
        projectSummaryResponseDTO.isFeatured( project.getIsFeatured() );
        projectSummaryResponseDTO.viewCount( project.getViewCount() );

        projectSummaryResponseDTO.skillNames( mapSkillNames(project.getSkills()) );

        return projectSummaryResponseDTO.build();
    }

    @Override
    public ProjectImageResponseDTO toImageResponseDTO(ProjectImage image) {
        if ( image == null ) {
            return null;
        }

        ProjectImageResponseDTO.ProjectImageResponseDTOBuilder projectImageResponseDTO = ProjectImageResponseDTO.builder();

        projectImageResponseDTO.id( image.getId() );
        projectImageResponseDTO.imageUrl( image.getImageUrl() );
        projectImageResponseDTO.altText( image.getAltText() );
        projectImageResponseDTO.displayOrder( image.getDisplayOrder() );

        return projectImageResponseDTO.build();
    }

    @Override
    public List<ProjectImageResponseDTO> toImageResponseDTOList(List<ProjectImage> images) {
        if ( images == null ) {
            return null;
        }

        List<ProjectImageResponseDTO> list = new ArrayList<ProjectImageResponseDTO>( images.size() );
        for ( ProjectImage projectImage : images ) {
            list.add( toImageResponseDTO( projectImage ) );
        }

        return list;
    }

    private String projectCategoryName(Project project) {
        Category category = project.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getName();
    }
}
