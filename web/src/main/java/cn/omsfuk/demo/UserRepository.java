package cn.omsfuk.demo;

import cn.omsfuk.smart.framework.core.annotation.Repository;
import cn.omsfuk.smart.framework.orm.User;
import cn.omsfuk.smart.framework.orm.annotation.Delete;
import cn.omsfuk.smart.framework.orm.annotation.Insert;
import cn.omsfuk.smart.framework.orm.annotation.Select;
import cn.omsfuk.smart.framework.orm.annotation.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by omsfuk on 17-5-30.
 */

@Repository
public interface UserRepository {

    @Select("select * from user where id = #{id}")
    cn.omsfuk.smart.framework.orm.User getUser(cn.omsfuk.smart.framework.orm.User user);

    @Select("select * from user")
    List<cn.omsfuk.smart.framework.orm.User> getUsers();

    @Insert("insert user(username, password) values(#{username}, #{password})")
    Integer insertUser(cn.omsfuk.smart.framework.orm.User user);

    @Delete("delete from user where id = #{id}")
    Integer deleteUser(Map<String, Object> map);

    @Update("update user set username = #{username}, password = #{password} where id = #{id}")
    Integer updateUser(User user);
}
