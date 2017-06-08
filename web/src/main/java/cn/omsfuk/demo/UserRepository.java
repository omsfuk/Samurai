package cn.omsfuk.demo;

import cn.omsfuk.samurai.framework.core.annotation.Repository;
import cn.omsfuk.samurai.framework.orm.annotation.Delete;
import cn.omsfuk.samurai.framework.orm.annotation.Insert;
import cn.omsfuk.samurai.framework.orm.annotation.Select;
import cn.omsfuk.samurai.framework.orm.annotation.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by omsfuk on 17-5-30.
 */

@Repository
public interface UserRepository {

    @Select("select * from user where id = #{id}")
    User getUser(User user);

    @Select("select * from user")
    List<User> getUsers();

    @Insert("insert user(username, password) values(#{username}, #{password})")
    Integer insertUser(User user);

    @Delete("delete from user where id = #{id}")
    Integer deleteUser(Map<String, Object> map);

    @Update("update user set username = #{username}, password = #{password} where id = #{id}")
    Integer updateUser(User user);
}
