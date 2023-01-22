CREATE TABLE goals (
    id UUID PRIMARY KEY,
    goal TEXT NOT NULL,
    goal_level TEXT NOT NULL,
    parent_ids UUID[],
    why TEXT,
    completed-percent BOOLEAN,


       canonical_source_id UUID PRIMARY KEY,
       domains text[] NOT NULL
);

CREATE TABLE twitter_rules (
       ,
       original_id TEXT NOT NULL,
       
       canonical_source_ids UUID[] NOT NULL
);