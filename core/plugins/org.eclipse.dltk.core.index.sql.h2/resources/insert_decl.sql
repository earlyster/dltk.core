-- IF YOU UPDATE THIS FILE, DON'T FORGET TO INCREMENT THE Schema.VERSION!
-- This is a prepared statement for inserting
-- an element declaration record. Table name
-- is substituted in a runtime.

INSERT INTO {0}(
	FLAGS,
	OFFSET,
	LENGTH,
	NAME_OFFSET,
	NAME_LENGTH,
	NAME,
	CC_NAME,
	METADATA,
	DOC,
	QUALIFIER,
	PARENT,FILE_ID)

VALUES(?,?,?,?,?,?,?,?,?,?,?,?);