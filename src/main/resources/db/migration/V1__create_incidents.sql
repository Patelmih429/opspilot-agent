create table incidents (
    id uuid primary key,
    title varchar(200) not null,
    description varchar(4000) not null,
    source varchar(100) not null,
    reported_severity varchar(20) not null,
    status varchar(20) not null,
    priority varchar(10),
    category varchar(100),
    summary varchar(2000),
    recommended_actions text,
    requires_human_review boolean,
    confidence double precision,
    triage_engine varchar(50),
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    version bigint not null default 0
);

create index incidents_created_at_idx on incidents (created_at desc);
create index incidents_status_idx on incidents (status);
