import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Column {
    String name();
}

class UserProfile {
    @Column(name = "user_id")
    private int id;

    @Column(name = "full_name")
    private String name;

    @Column(name = "email_address")
    private String email;

    @Column(name = "phone_number")
    private String phone;

    @Override
    public String toString() {
        return "UserProfile { id=" + id + ", name='" + name + "', email='" + email + "', phone='" + phone + "' }";
    }
}

class RowMapper {
    public static Object mapRow(Class clazz, String[] headers, String[] values) throws Exception {
        Object instance = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column col = (Column) field.getAnnotation(Column.class);
                String columnName = col.name();

                field.setAccessible(true);
                String foundValue = null;

                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].equalsIgnoreCase(columnName) && i < values.length) {
                        foundValue = values[i];
                        break;
                    }
                }

                if (foundValue != null) {
                    if (field.getType() == int.class) {
                        field.set(instance, Integer.parseInt(foundValue));
                    } else if (field.getType() == double.class) {
                        field.set(instance, Double.parseDouble(foundValue));
                    } else {
                        field.set(instance, foundValue);
                    }
                } else {
                    System.out.println("[Warning] Missing column: '" + columnName + "' for field: " + field.getName());
                }
            }
        }

        return instance;
    }
}

public class ColumnMapperDemo {
    public static void main(String[] args) {
        String[] headers = { "user_id", "full_name", "email_address" };
        String[] rowData = { "101", "John Doe", "john.doe@example.com" };

        try {
            UserProfile profile = (UserProfile) RowMapper.mapRow(UserProfile.class, headers, rowData);
            System.out.println("Mapped Object:");
            System.out.println(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}