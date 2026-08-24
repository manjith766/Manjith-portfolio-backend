-- =====================================================================
-- V2__seed_data.sql
-- Bootstrap data required for the application to function on first boot.
-- No project/skill/experience content seeded here — that's real content
-- managed via the Admin Dashboard, not migration-baked fixtures.
-- =====================================================================

-- ---------------------------------------------------------------------
-- ROLES
-- ---------------------------------------------------------------------
INSERT INTO roles (name) VALUES ('ADMIN'), ('USER');

-- ---------------------------------------------------------------------
-- ADMIN USER
-- Password is a bcrypt hash — placeholder hash below MUST be replaced
-- before deploying. Generate with: new BCryptPasswordEncoder().encode("yourPassword")
-- Not seeding a real password value here since that would bake a known
-- credential into version control.
-- ---------------------------------------------------------------------
INSERT INTO users (username, email, password_hash, role_id, enabled)
VALUES (
    'admin',
    'manjith9989@gmail.com',
    '$2a$10$REPLACE_WITH_REAL_BCRYPT_HASH_BEFORE_DEPLOY',
    (SELECT id FROM roles WHERE name = 'ADMIN'),
    TRUE
);

-- ---------------------------------------------------------------------
-- DEFAULT CATEGORIES (SKILL type) — matches skill groups from spec
-- ---------------------------------------------------------------------
INSERT INTO categories (name, slug, type) VALUES
    ('Backend', 'backend', 'SKILL'),
    ('Frontend', 'frontend', 'SKILL'),
    ('Database', 'database', 'SKILL'),
    ('DevOps & Cloud', 'devops-cloud', 'SKILL'),
    ('Tools & Practices', 'tools-practices', 'SKILL'),
    ('Java & Spring', 'java-spring', 'BLOG'),
    ('System Design', 'system-design', 'BLOG'),
    ('Career', 'career', 'BLOG'),
    ('Backend', 'backend-project', 'PROJECT'),
    ('Full Stack', 'full-stack', 'PROJECT'),
    ('Security', 'security', 'PROJECT');

-- ---------------------------------------------------------------------
-- DEFAULT SOCIAL LINKS — sourced from uploaded profile
-- ---------------------------------------------------------------------
INSERT INTO social_links (platform, url, icon, display_order) VALUES
    ('GITHUB', 'https://github.com/manjith766/Manjith-portfolio', 'github', 1),
    ('LINKEDIN', 'https://www.linkedin.com/in/manjithnagineni', 'linkedin', 2),
    ('EMAIL', 'mailto:manjith9989@gmail.com', 'mail', 3),
    ('PHONE', 'tel:+919398303933', 'phone', 4);

-- ---------------------------------------------------------------------
-- DEFAULT SITE SETTINGS
-- ---------------------------------------------------------------------
INSERT INTO settings (setting_key, setting_value) VALUES
    ('site.title', 'Manjith Nagineni | Java Full Stack Developer'),
    ('site.tagline', 'Java Full Stack Developer | Spring Boot | Microservices'),
    ('site.theme.default', 'dark'),
    ('site.availability_status', 'AVAILABLE'),
    ('site.years_experience', '2'),
    ('site.location', 'Hyderabad, Telangana, India');

-- ---------------------------------------------------------------------
-- CERTIFICATIONS — sourced from uploaded profile. issue_date is
-- deliberately approximate (only month/year-level info was available on
-- the profile, no exact day) — update via admin UI once exact dates are
-- known rather than trusting these placeholders indefinitely.
-- ---------------------------------------------------------------------
INSERT INTO certifications (title, issuer, issue_date, credential_url, display_order) VALUES
    ('Software Engineering Job Simulation', 'JPMorganChase', '2025-01-01', NULL, 1),
    ('Introduction to Programming Using Java', 'Coursera', '2024-06-01', NULL, 2),
    ('Solutions Architecture Job Simulation', 'AWS', '2025-03-01', NULL, 3);

-- ---------------------------------------------------------------------
-- DEFAULT SEO ROWS — one per top-level page
-- ---------------------------------------------------------------------
INSERT INTO seo (page_path, meta_title, meta_description) VALUES
    ('/', 'Manjith Nagineni | Java Full Stack Developer', 'Java Full Stack Developer specializing in Spring Boot, Spring Security, JWT authentication, Hibernate, and microservices.'),
    ('/about', 'About | Manjith Nagineni', 'Java Full Stack Developer with 2 years of experience building secure, scalable backend systems.'),
    ('/skills', 'Skills | Manjith Nagineni', 'Technical skills in Java, Spring Boot, Spring Security, Hibernate, React, Docker, and SQL.'),
    ('/projects', 'Projects | Manjith Nagineni', 'Production-grade projects including a multi-vendor e-commerce platform with RBAC and transactional order workflows.'),
    ('/experience', 'Experience | Manjith Nagineni', 'Professional experience as a Java Full Stack Developer.'),
    ('/education', 'Education | Manjith Nagineni', 'Educational background including MCA from Prakasam Engineering College.'),
    ('/certifications', 'Certifications | Manjith Nagineni', 'Professional certifications including JPMorganChase and AWS job simulations.'),
    ('/blog', 'Blog | Manjith Nagineni', 'Articles on Java, Spring Boot, backend architecture, and software engineering practices.'),
    ('/contact', 'Contact | Manjith Nagineni', 'Get in touch with Manjith Nagineni for opportunities and collaboration.');
