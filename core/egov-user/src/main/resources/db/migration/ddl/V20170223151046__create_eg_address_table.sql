CREATE TABLE eg_address (
    housenobldgapt character varying(32),
    subdistrict character varying(100),
    postoffice character varying(100),
    landmark character varying(256),
    country character varying(50),
    userid bigint not null references eg_user(id),
    type character varying(50),
    streetroadline character varying(256),
    citytownvillage character varying(256),
    arealocalitysector character varying(256),
    district character varying(100),
    state character varying(100),
    pincode character varying(10),
    id serial NOT NULL primary key,
    version bigint DEFAULT 0,
    tenantid character varying(256) not null);