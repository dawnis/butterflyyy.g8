CREATE TABLE IF NOT EXISTS "users" (
                       "id" SERIAL PRIMARY KEY,
                       "name" VARCHAR NOT NULL,
                       "age" INT NOT NULL,
                       "country" VARCHAR
);
