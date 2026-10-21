import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface NotBlank {
    String message() default "Field cannot be blank";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface MaxLength {
    int value();
    String message() default "Field exceeds maximum allowed length";
}

class SignupForm {
    @NotBlank(message = "Username cannot be empty")
    @MaxLength(value = 10, message = "Username cannot exceed 10 characters")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    private String email;

    @MaxLength(value = 8, message = "Password cannot exceed 8 characters")
    private String password;

    SignupForm(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}

class FormValidator {
    public static List validate(Object obj) {
        List errors = new ArrayList();
        Class clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object valueObj = field.get(obj);
                String value = (valueObj == null) ? null : valueObj.toString();

                if (field.isAnnotationPresent(NotBlank.class)) {
                    NotBlank notBlank = field.getAnnotation(NotBlank.class);
                    if (value == null || value.trim().isEmpty()) {
                        errors.add(field.getName() + ": " + notBlank.message());
                    }
                }

                if (field.isAnnotationPresent(MaxLength.class)) {
                    MaxLength maxLength = field.getAnnotation(MaxLength.class);
                    if (value != null && value.length() > maxLength.value()) {
                        errors.add(field.getName() + ": " + maxLength.message() + " (max: " + maxLength.value() + ")");
                    }
                }
            } catch (IllegalAccessException e) {
                errors.add("Error accessing field: " + field.getName());
            }
        }
        return errors;
    }
}

public class FormValidatorDemo {
    public static void main(String[] args) {
        SignupForm form = new SignupForm("superlongusername123", "   ", "validPass12345");
        List errors = FormValidator.validate(form);

        if (errors.isEmpty()) {
            System.out.println("Form is valid!");
        } else {
            System.out.println("Validation failed with errors:");
            for (Object error : errors) {
                System.out.println("- " + error);
            }
        }
    }
}