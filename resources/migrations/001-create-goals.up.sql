CREATE TABLE goals (
    id UUID PRIMARY KEY,
    goal TEXT NOT NULL,
    goal_level TEXT NOT NULL,
    parent_ids UUID[],
    why TEXT,
    completed-percent BOOLEAN
);
