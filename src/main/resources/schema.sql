
CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description VARCHAR(512) NOT NULL,
  requestor_id BIGINT NOT NULL,
  CONSTRAINT fk_requestor FOREIGN KEY(requestor_id) REFERENCES users(id),
  CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  is_available boolean NOT NULL,
  owner_id BIGINT NOT NULL,
  request_id BIGINT,
  CONSTRAINT fk_owner FOREIGN KEY(owner_id) REFERENCES users(id),
  CONSTRAINT fk_request FOREIGN KEY(request_id) REFERENCES requests(id),
  CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  status VARCHAR NOT NULL,
  item_id BIGINT NOT NULL,
  booker_id BIGINT NOT NULL,
  CONSTRAINT fk_item FOREIGN KEY(item_id) REFERENCES items(id),
  CONSTRAINT fk_booker FOREIGN KEY(booker_id) REFERENCES users(id),
  CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text VARCHAR(512) NOT NULL,
  item_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT fk_item_comment FOREIGN KEY(item_id) REFERENCES items(id),
  CONSTRAINT fk_author FOREIGN KEY(author_id) REFERENCES users(id),
  CONSTRAINT pk_comment PRIMARY KEY (id)
);