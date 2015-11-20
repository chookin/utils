package cmri.utils.lang;

import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Ignore;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zhuyin on 3/19/15.
 */
public class TestJsonHelper extends TestCase {
    public void testObject(){
        // ref: https://github.com/alibaba/fastjson/wiki/Samples-DataBind
        User rootUser = new User();
        rootUser.setId(3L);
        rootUser.setName("root");
        String jsonString = JSON.toJSONString(rootUser);
        System.out.println(jsonString);

        User parsedUser = JSON.parseObject(jsonString, User.class);
        assertEquals(rootUser, parsedUser);

        Group group = new Group();
        group.setId(0L);
        group.setName("admin");

        User guestUser = new User();
        guestUser.setId(2L);
        guestUser.setName("guest");

        group.addUser(guestUser);
        group.addUser(rootUser);

        jsonString = JSON.toJSONString(group);
        System.out.println(jsonString);
        Group parsedGroup = JsonHelper.parseObject(jsonString, Group.class);
        assertEquals(group, parsedGroup);
    }

    // if inner class, then must be static, or else will throw NullPointerException when deserialization.
    static class User {

        private Long   id;
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof User)) return false;

            User user = (User) o;

            if (id != null ? !id.equals(user.id) : user.id != null) return false;
            if (name != null ? !name.equals(user.name) : user.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
    static class Group {

        private Long       id;
        private String     name;
        private Map<Long, User> users = new TreeMap<>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // warn: if no get method, then users will not be serialized.
        public Map<Long, User> getUsers() {
            return users;
        }

        public void addUser(User user){
            this.users.put(user.id, user);
        }

        public void addUser(Collection<User> users) {
            users.forEach(user -> this.users.put(user.id, user));
        }

        @Override
        public String toString() {
            return "Group{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", users=" + users +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Group)) return false;

            Group group = (Group) o;

            if (id != null ? !id.equals(group.id) : group.id != null) return false;
            if (name != null ? !name.equals(group.name) : group.name != null) return false;
            if (users != null ? !users.equals(group.users) : group.users != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (users != null ? users.hashCode() : 0);
            return result;
        }
    }
}
