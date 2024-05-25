CREATE TABLE assessments (
    id SERIAL PRIMARY KEY,
    assessment_id UUID NOT NULL,
    assessment_date DATE NOT NULL,
    assessor_name VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    notes TEXT
);

INSERT INTO assessments (assessment_id, assessment_date, assessor_name, status, notes) 
VALUES 
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '2022-01-01', 'John Doe', 'Good', 'No floors or windows but livable'),
('b3ba3d88-f441-45fa-a5d2-8a8b9a793b73', '2024-02-01', 'Jane Smith', 'Needs Improvement', 'The roof is missing'),
('c6c22b64-7e8a-45b9-8a7e-4a7a3b64c189', '2022-03-01', 'John Doe', 'Excellent', 'Best house I have ever seen'),
('d7d9e39a-5e1f-46a0-8f8e-77a8a596b7a2', '2021-04-01', 'Jane Smith', 'Poor', 'Not a great property');
