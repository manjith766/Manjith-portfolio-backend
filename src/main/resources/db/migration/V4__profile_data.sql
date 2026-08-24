-- =====================================================================
-- V3__profile_data.sql
-- Portfolio profile content
--
-- IMPORTANT:
-- V2__seed_data.sql is already applied.
-- DO NOT MODIFY V2.
--
-- This migration adds:
--   - Skills
--   - Multi-Vendor E-Commerce Platform
--   - Project skills
--   - Project features
--   - Experience
--   - Experience responsibilities
--   - Experience skills
--   - Education
--   - Updated certifications
-- =====================================================================


-- =====================================================================
-- 1. SKILLS
-- =====================================================================

INSERT INTO skills (
    name,
    category_id,
    proficiency_pct,
    years_experience,
    icon_url,
    display_order
)
VALUES

-- ---------------------------------------------------------------------
-- Backend
-- ---------------------------------------------------------------------

(
    'Java',
    (SELECT id FROM categories
     WHERE slug = 'backend' AND type = 'SKILL'),
    95,
    2.0,
    NULL,
    1
),

(
    'Spring Boot',
    (SELECT id FROM categories
     WHERE slug = 'backend' AND type = 'SKILL'),
    92,
    2.0,
    NULL,
    2
),

(
    'Spring Security',
    (SELECT id FROM categories
     WHERE slug = 'backend' AND type = 'SKILL'),
    88,
    1.5,
    NULL,
    3
),

(
    'Hibernate',
    (SELECT id FROM categories
     WHERE slug = 'backend' AND type = 'SKILL'),
    88,
    2.0,
    NULL,
    4
),

(
    'JPA',
    (SELECT id FROM categories
     WHERE slug = 'backend' AND type = 'SKILL'),
    88,
    2.0,
    NULL,
    5
),

(
    'REST APIs',
    (SELECT id FROM categories
     WHERE slug = 'backend' AND type = 'SKILL'),
    92,
    2.0,
    NULL,
    6
),

(
    'Microservices',
    (SELECT id FROM categories
     WHERE slug = 'backend' AND type = 'SKILL'),
    82,
    1.5,
    NULL,
    7
),

(
    'JWT',
    (SELECT id FROM categories
     WHERE slug = 'backend' AND type = 'SKILL'),
    85,
    1.5,
    NULL,
    8
),

(
    'RBAC',
    (SELECT id FROM categories
     WHERE slug = 'backend' AND type = 'SKILL'),
    82,
    1.5,
    NULL,
    9
),

(
    'Collections Framework',
    (SELECT id FROM categories
     WHERE slug = 'backend' AND type = 'SKILL'),
    85,
    2.0,
    NULL,
    10
),

(
    'Data Structures',
    (SELECT id FROM categories
     WHERE slug = 'backend' AND type = 'SKILL'),
    78,
    2.0,
    NULL,
    11
),

(
    'Exception Handling',
    (SELECT id FROM categories
     WHERE slug = 'backend' AND type = 'SKILL'),
    88,
    2.0,
    NULL,
    12
),


-- ---------------------------------------------------------------------
-- Frontend
-- ---------------------------------------------------------------------

(
    'HTML',
    (SELECT id FROM categories
     WHERE slug = 'frontend' AND type = 'SKILL'),
    78,
    1.5,
    NULL,
    13
),

(
    'CSS',
    (SELECT id FROM categories
     WHERE slug = 'frontend' AND type = 'SKILL'),
    75,
    1.5,
    NULL,
    14
),

(
    'JavaScript',
    (SELECT id FROM categories
     WHERE slug = 'frontend' AND type = 'SKILL'),
    72,
    1.5,
    NULL,
    15
),


-- ---------------------------------------------------------------------
-- Database
-- ---------------------------------------------------------------------

(
    'SQL',
    (SELECT id FROM categories
     WHERE slug = 'database' AND type = 'SKILL'),
    88,
    2.0,
    NULL,
    16
),

(
    'MySQL',
    (SELECT id FROM categories
     WHERE slug = 'database' AND type = 'SKILL'),
    88,
    2.0,
    NULL,
    17
),


-- ---------------------------------------------------------------------
-- DevOps & Cloud
-- ---------------------------------------------------------------------

(
    'Docker',
    (SELECT id FROM categories
     WHERE slug = 'devops-cloud' AND type = 'SKILL'),
    75,
    1.0,
    NULL,
    18
),


-- ---------------------------------------------------------------------
-- Tools & Practices
-- ---------------------------------------------------------------------

(
    'JUnit',
    (SELECT id FROM categories
     WHERE slug = 'tools-practices' AND type = 'SKILL'),
    78,
    1.5,
    NULL,
    19
),

(
    'SOLID',
    (SELECT id FROM categories
     WHERE slug = 'tools-practices' AND type = 'SKILL'),
    85,
    2.0,
    NULL,
    20
),

(
    'Clean Architecture',
    (SELECT id FROM categories
     WHERE slug = 'tools-practices' AND type = 'SKILL'),
    85,
    1.5,
    NULL,
    21
);


-- =====================================================================
-- 2. PROJECT
-- =====================================================================

INSERT INTO projects (
    title,
    slug,
    description,
    short_description,
    category_id,
    github_url,
    live_demo_url,
    cover_image_url,
    is_featured,
    display_order,
    view_count
)
VALUES (
    'Multi-Vendor E-Commerce Platform',
    'multi-vendor-e-commerce-platform',

    'A production-grade multi-vendor e-commerce platform designed and shipped with secure backend architecture, role-based access control, transactional order processing, coupon management, and encrypted image moderation.',

    'Production-grade multi-vendor e-commerce platform with RBAC, transactional cart-to-order workflow, coupon engine, and secure image moderation.',

    (
        SELECT id
        FROM categories
        WHERE slug = 'full-stack'
          AND type = 'PROJECT'
    ),

    NULL,
    NULL,
    NULL,
    TRUE,
    1,
    0
);


-- =====================================================================
-- 3. PROJECT SKILLS
-- =====================================================================

INSERT INTO project_skills (
    project_id,
    skill_id
)
SELECT
    p.id,
    s.id
FROM projects p
JOIN skills s
    ON s.name IN (
        'Java',
        'Spring Boot',
        'Spring Security',
        'Hibernate',
        'JPA',
        'REST APIs',
        'Microservices',
        'JWT',
        'RBAC',
        'SQL',
        'MySQL',
        'Docker',
        'JUnit',
        'SOLID',
        'Clean Architecture'
    )
WHERE p.slug = 'multi-vendor-e-commerce-platform';


-- =====================================================================
-- 4. PROJECT FEATURES
-- =====================================================================

INSERT INTO project_features (
    project_id,
    feature_text,
    display_order
)
VALUES

(
    (
        SELECT id
        FROM projects
        WHERE slug = 'multi-vendor-e-commerce-platform'
    ),
    '20+ production-grade REST APIs',
    1
),

(
    (
        SELECT id
        FROM projects
        WHERE slug = 'multi-vendor-e-commerce-platform'
    ),
    'Customer, Seller, and Admin role-based access control',
    2
),

(
    (
        SELECT id
        FROM projects
        WHERE slug = 'multi-vendor-e-commerce-platform'
    ),
    'Complete cart-to-order workflow',
    3
),

(
    (
        SELECT id
        FROM projects
        WHERE slug = 'multi-vendor-e-commerce-platform'
    ),
    'Transactional consistency during order processing',
    4
),

(
    (
        SELECT id
        FROM projects
        WHERE slug = 'multi-vendor-e-commerce-platform'
    ),
    'Coupon engine with real-world edge-case handling',
    5
),

(
    (
        SELECT id
        FROM projects
        WHERE slug = 'multi-vendor-e-commerce-platform'
    ),
    'AES-encrypted image moderation system',
    6
),

(
    (
        SELECT id
        FROM projects
        WHERE slug = 'multi-vendor-e-commerce-platform'
    ),
    'Method-level security',
    7
),

(
    (
        SELECT id
        FROM projects
        WHERE slug = 'multi-vendor-e-commerce-platform'
    ),
    'Clean layered architecture following SOLID principles',
    8
);


-- =====================================================================
-- 5. EXPERIENCE
-- =====================================================================


-- ---------------------------------------------------------------------
-- JIPPY MART
-- ---------------------------------------------------------------------

INSERT INTO experience (
    company_name,
    role,
    location,
    start_date,
    end_date,
    is_current,
    description,
    display_order
)
VALUES (
    'Jippy Mart',
    'Java Full Stack Developer',
    'Hyderabad',
    '2026-04-01',
    NULL,
    TRUE,
    'Java Full Stack Developer building secure and scalable backend systems using Java, Spring Boot, Spring Security, REST APIs, and related enterprise technologies.',
    1
);


-- ---------------------------------------------------------------------
-- NEOTERIC METHODS
-- ---------------------------------------------------------------------

INSERT INTO experience (
    company_name,
    role,
    location,
    start_date,
    end_date,
    is_current,
    description,
    display_order
)
VALUES (
    'Neoteric Methods',
    'Java Backend Developer – Training & Projects',
    'Hyderabad',
    '2025-01-01',
    '2025-11-30',
    FALSE,
    'Java Backend Development Trainee working on Spring Boot based backend applications and project-based development.',
    2
);


-- =====================================================================
-- 6. JIPPY MART RESPONSIBILITIES
-- =====================================================================

INSERT INTO experience_responsibilities (
    experience_id,
    text,
    display_order
)
VALUES

(
    (
        SELECT id
        FROM experience
        WHERE company_name = 'Jippy Mart'
          AND role = 'Java Full Stack Developer'
    ),
    'Designed and shipped a multi-vendor e-commerce platform from scratch.',
    1
),

(
    (
        SELECT id
        FROM experience
        WHERE company_name = 'Jippy Mart'
          AND role = 'Java Full Stack Developer'
    ),
    'Developed 20+ production-grade REST APIs.',
    2
),

(
    (
        SELECT id
        FROM experience
        WHERE company_name = 'Jippy Mart'
          AND role = 'Java Full Stack Developer'
    ),
    'Implemented Customer, Seller, and Admin role-based access control.',
    3
),

(
    (
        SELECT id
        FROM experience
        WHERE company_name = 'Jippy Mart'
          AND role = 'Java Full Stack Developer'
    ),
    'Implemented the complete cart-to-order workflow with transactional consistency.',
    4
),

(
    (
        SELECT id
        FROM experience
        WHERE company_name = 'Jippy Mart'
          AND role = 'Java Full Stack Developer'
    ),
    'Developed a coupon engine handling real-world edge cases.',
    5
),

(
    (
        SELECT id
        FROM experience
        WHERE company_name = 'Jippy Mart'
          AND role = 'Java Full Stack Developer'
    ),
    'Implemented an AES-encrypted image moderation system with method-level security.',
    6
),

(
    (
        SELECT id
        FROM experience
        WHERE company_name = 'Jippy Mart'
          AND role = 'Java Full Stack Developer'
    ),
    'Followed clean layered architecture and SOLID principles.',
    7
);


-- =====================================================================
-- 7. NEOTERIC METHODS RESPONSIBILITIES
-- =====================================================================

INSERT INTO experience_responsibilities (
    experience_id,
    text,
    display_order
)
VALUES

(
    (
        SELECT id
        FROM experience
        WHERE company_name = 'Neoteric Methods'
          AND role = 'Java Backend Developer – Training & Projects'
    ),
    'Developed REST APIs using Spring Boot following Controller-Service-Repository architecture.',
    1
),

(
    (
        SELECT id
        FROM experience
        WHERE company_name = 'Neoteric Methods'
          AND role = 'Java Backend Developer – Training & Projects'
    ),
    'Implemented database operations using JPA/Hibernate with MySQL.',
    2
),

(
    (
        SELECT id
        FROM experience
        WHERE company_name = 'Neoteric Methods'
          AND role = 'Java Backend Developer – Training & Projects'
    ),
    'Designed backend modules for scalable applications.',
    3
),

(
    (
        SELECT id
        FROM experience
        WHERE company_name = 'Neoteric Methods'
          AND role = 'Java Backend Developer – Training & Projects'
    ),
    'Practiced API testing and debugging.',
    4
),

(
    (
        SELECT id
        FROM experience
        WHERE company_name = 'Neoteric Methods'
          AND role = 'Java Backend Developer – Training & Projects'
    ),
    'Built backend features for project-based applications.',
    5
);


-- =====================================================================
-- 8. JIPPY MART SKILLS
-- =====================================================================

INSERT INTO experience_skills (
    experience_id,
    skill_id
)
SELECT
    e.id,
    s.id
FROM experience e
JOIN skills s
    ON s.name IN (
        'Java',
        'Spring Boot',
        'Spring Security',
        'JPA',
        'Hibernate',
        'REST APIs',
        'Microservices',
        'JWT',
        'RBAC',
        'SQL',
        'Docker',
        'JUnit',
        'SOLID',
        'Clean Architecture'
    )
WHERE e.company_name = 'Jippy Mart'
  AND e.role = 'Java Full Stack Developer';


-- =====================================================================
-- 9. NEOTERIC METHODS SKILLS
-- =====================================================================

INSERT INTO experience_skills (
    experience_id,
    skill_id
)
SELECT
    e.id,
    s.id
FROM experience e
JOIN skills s
    ON s.name IN (
        'Java',
        'Spring Boot',
        'REST APIs',
        'JPA',
        'Hibernate',
        'MySQL',
        'SQL'
    )
WHERE e.company_name = 'Neoteric Methods'
  AND e.role = 'Java Backend Developer – Training & Projects';


-- =====================================================================
-- 10. EDUCATION
-- =====================================================================


-- ---------------------------------------------------------------------
-- MCA
-- ---------------------------------------------------------------------

INSERT INTO education (
    degree,
    institution,
    location,
    start_date,
    end_date,
    cgpa,
    description,
    display_order
)
VALUES (
    'Master of Computer Applications',
    'Prakasam Engineering College',
    'Andhra Pradesh, India',
    '2023-05-01',
    '2025-05-31',
    NULL,
    'Computer Programming, Specific Applications',
    1
);


-- ---------------------------------------------------------------------
-- BACHELOR OF COMPUTER SCIENCE
-- ---------------------------------------------------------------------

INSERT INTO education (
    degree,
    institution,
    location,
    start_date,
    end_date,
    cgpa,
    description,
    display_order
)
VALUES (
    'Bachelor of Computer Science',
    'Jagarlamudi Kuppuswamy Choudary College (JKC College)',
    'Andhra Pradesh, India',
    '2020-05-01',
    '2023-04-30',
    NULL,
    'Mathematics and Computer Science',
    2
);


-- ---------------------------------------------------------------------
-- INTERMEDIATE
-- ---------------------------------------------------------------------

INSERT INTO education (
    degree,
    institution,
    location,
    start_date,
    end_date,
    cgpa,
    description,
    display_order
)
VALUES (
    'Intermediate',
    'Sri Pratibha Junior College',
    'Andhra Pradesh, India',
    '2018-01-01',
    '2020-12-31',
    NULL,
    NULL,
    3
);


-- ---------------------------------------------------------------------
-- HIGH SCHOOL
-- ---------------------------------------------------------------------

INSERT INTO education (
    degree,
    institution,
    location,
    start_date,
    end_date,
    cgpa,
    description,
    display_order
)
VALUES (
    'High School Diploma',
    'Siddardha High School',
    'Andhra Pradesh, India',
    '2017-01-01',
    '2018-12-31',
    NULL,
    NULL,
    4
);


-- =====================================================================
-- 11. CERTIFICATIONS
--
-- LinkedIn/profile data:
--
-- Introduction to Programming Using Java
-- Infosys Springboard - April 2026
--
-- AWS For Beginners
-- Great Learning - March 2026
--
-- JPMorganChase - Software Engineering Job Simulation
-- Forage - December 2025
--
-- AWS - Solutions Architecture Job Simulation
-- Forage - November 2025
--
-- Structured Query Language (SQL)
-- Parishkar Technologies - September 2024
--
-- Salesforce Developer Virtual Internship
-- Sales Partners - August 2022
--
-- LinkedIn provides month/year. The first day of each month is used
-- because certifications.issue_date requires a complete DATE.
-- =====================================================================

INSERT INTO certifications (
    title,
    issuer,
    issue_date,
    credential_url,
    image_url,
    display_order
)
VALUES

(
    'Introduction to Programming Using Java',
    'Infosys Springboard',
    '2026-04-01',
    NULL,
    NULL,
    1
),

(
    'AWS For Beginners',
    'Great Learning',
    '2026-03-01',
    NULL,
    NULL,
    2
),

(
    'JPMorganChase - Software Engineering Job Simulation',
    'Forage',
    '2025-12-01',
    NULL,
    NULL,
    3
),

(
    'AWS - Solutions Architecture Job Simulation',
    'Forage',
    '2025-11-01',
    NULL,
    NULL,
    4
),

(
    'Structured Query Language (SQL)',
    'Parishkar Technologies',
    '2024-09-01',
    NULL,
    NULL,
    5
),

(
    'Salesforce Developer Virtual Internship',
    'Sales Partners',
    '2022-08-01',
    NULL,
    NULL,
    6
);