CREATE TABLE IF NOT EXISTS profiles (
    user_id   serial PRIMARY KEY,
    name      text NOT NULL,
    password  text NOT NULL
);

INSERT INTO profiles(name, password)
VALUES
  ('admin', 'password'),
  ('user',  'user123');

CREATE TABLE IF NOT EXISTS topics (
  id        serial PRIMARY KEY,
  name      text NOT NULL UNIQUE,
  path      text NOT NULL UNIQUE,
  image_url text NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS content (
  id              serial PRIMARY KEY,
  topic_id        integer NOT NULL,
  content_chapter text,
  content_text    text,
  FOREIGN KEY (topic_id) REFERENCES topics (id)
);

INSERT INTO topics(name, path, image_url)
VALUES
  ('Object-Oriented Programming', 'oop',   'https://thumbs.dreamstime.com/b/coding-object-oriented-programming-blue-background-color-white-text-chars-digits-matrix-byte-binary-data-rian-code-269116445.jpg'),
  ('Data Structures',             'ds',    'https://static.thenounproject.com/png/1473724-200.png'),
  ('Algorithms',                  'algo',  'https://static.vecteezy.com/system/resources/previews/023/752/967/non_2x/algorithm-icon-in-illustration-vector.jpg'),
  ('Visualization',               'visual','https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEiwMnAjlz3nFATjQkc1DoiKi_zyp-NVCc6r5G8eTOUp1_3hT-8FBYMFLuooxdXINecgpz_Gnkxf-ZUE3X1-gEHeDcOQzWun9VXnv5kk7kspwHS7Nz6GVQqWX2AGGnby5ry2AdjFEPqiJdM/s1600/binary-tree-1-search.gif');

INSERT INTO content(topic_id, content_chapter, content_text)
VALUES
  (1, 'chapter 1 OOP',  'chapter 1 OOP TEXT'),
  (1, 'chapter 2 OOP',  'chapter 2 OOP TEXT'),
  (1, 'chapter 3 OOP',  'chapter 3 OOP TEXT'),

  (2, 'chapter 1 DS',   'chapter 1 DS TEXT'),
  (2, 'chapter 2 DS',   'chapter 2 DS TEXT'),
  (2, 'chapter 3 DS',   'chapter 3 DS TEXT'),

  (3, 'chapter 1 ALGO', 'chapter 1 ALGO TEXT'),
  (3, 'chapter 2 ALGO', 'chapter 2 ALGO TEXT'),
  (3, 'chapter 3 ALGO', 'chapter 3 ALGO TEXT'),

  (4, 'Test balancable binary tree', 'main.py');
