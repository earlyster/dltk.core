-- IF YOU UPDATE THIS FILE, DON'T FORGET TO INCREMENT THE Schema.VERSION!
-- This is a prepared statement for inserting
-- an element reference record. Table name
-- is substituted in a runtime.

INSERT INTO {0}(
	OFFSET,
	LENGTH,
	NAME,
	METADATA,
	QUALIFIER,
	FILE_ID)

VALUES(?,?,?,?,?,?);