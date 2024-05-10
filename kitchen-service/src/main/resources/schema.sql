CREATE TABLE IF NOT EXISTS cook (
    id SERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS recipe (
    id SERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    description TEXT NOT NULL,
    cooking_time INTERVAL NOT NULL,
    portion_size_amount NUMERIC(9, 2) NOT NULL,
    portion_size_measure VARCHAR(32) NOT NULL,
    cook_id INT NOT NULL,
    CONSTRAINT FK_COOK FOREIGN KEY (cook_id) REFERENCES cook(id)
);

CREATE TABLE IF NOT EXISTS ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(256) NOT NULL,
    amount NUMERIC(9, 2) NOT NULL,
    measure VARCHAR(32) NOT NULL,
    position INT NOT NULL,
    recipe_id INT NOT NULL,
    CONSTRAINT FK_RECIPE FOREIGN KEY (recipe_id) REFERENCES recipe(id)
);

CREATE TABLE IF NOT EXISTS step (
    id SERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    position INT NOT NULL,
    recipe_id INT NOT NULL,
    CONSTRAINT FK_RECIPE FOREIGN KEY (recipe_id) REFERENCES recipe(id)
);


