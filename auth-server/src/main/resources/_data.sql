INSERT INTO "public"."sys_authority" VALUES ('2', 'wyf', '\254\355\000\005sr\000\015java.time.Ser\225]\204\272\033"H\262\014\000\000xpw\015\002\000\000\000\000Y@\257A\020\277\270@x', null, '\254\355\000\005sr\000\015java.time.Ser\225]\204\272\033"H\262\014\000\000xpw\015\002\000\000\000\000Y@\257A\020\277\270@x', '查看demo', 'query-demo');



INSERT INTO "public"."sys_role" VALUES ('3', 'wyf', '\254\355\000\005sr\000\015java.time.Ser\225]\204\272\033"H\262\014\000\000xpw\015\002\000\000\000\000Y@\257A\025V\245\200x', null, '\254\355\000\005sr\000\015java.time.Ser\225]\204\272\033"H\262\014\000\000xpw\015\002\000\000\000\000Y@\257A\025V\245\200x', '管理员', 'ROLE_ADMIN');
INSERT INTO "public"."sys_role" VALUES ('4', 'wyf', '\254\355\000\005sr\000\015java.time.Ser\225]\204\272\033"H\262\014\000\000xpw\015\002\000\000\000\000Y@\257A\026\324\035\300x', null, '\254\355\000\005sr\000\015java.time.Ser\225]\204\272\033"H\262\014\000\000xpw\015\002\000\000\000\000Y@\257A\026\324\035\300x', '普通用户', 'ROLE_USER');


INSERT INTO "public"."sys_role_authorities" VALUES ('3', '2');




INSERT INTO "public"."sys_user" VALUES ('5', 'wyf', '\254\355\000\005sr\000\015java.time.Ser\225]\204\272\033"H\262\014\000\000xpw\015\002\000\000\000\000Y@\257A\027{\366\200x', null, '\254\355\000\005sr\000\015java.time.Ser\225]\204\272\033"H\262\014\000\000xpw\015\002\000\000\000\000Y@\257A\027{\366\200x', null, null, null, null, '$2a$10$XOVs4Z1YtPKqKwQVywG9j.xLAqXYRQLGZMGMrZDNbtl6pUC0Weteq', 'admin');
INSERT INTO "public"."sys_user" VALUES ('6', 'wyf', '\254\355\000\005sr\000\015java.time.Ser\225]\204\272\033"H\262\014\000\000xpw\015\002\000\000\000\000Y@\257A \021:\200x', null, '\254\355\000\005sr\000\015java.time.Ser\225]\204\272\033"H\262\014\000\000xpw\015\002\000\000\000\000Y@\257A \021:\200x', null, null, null, null, '$2a$10$WjApX3bMw1KfzckGCCOB.eXRNY61ZcwsqpNzc2yiHtjsqnS3LmXAS', 'wyf');


BEGIN;
INSERT INTO "public"."sys_user_roles" VALUES ('5', '3');
INSERT INTO "public"."sys_user_roles" VALUES ('6', '4');

