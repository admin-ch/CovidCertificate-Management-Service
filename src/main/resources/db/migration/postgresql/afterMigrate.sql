create or replace procedure reassign_objects_ownership()
LANGUAGE 'plpgsql'
as $BODY$
BEGIN
 execute format('reassign owned by %s to %s_role_full', user, current_database());
END
$BODY$;

call reassign_objects_ownership();

drop procedure reassign_objects_ownership();
