//package caramel.stratego.user;
//
//import caramel.stratego.user.User;
//import caramel.stratego.user.service.UserDomainService;
//import caramel.stratego.user.storage.UserRepository;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.MockitoAnnotations.initMocks;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class UserServiceTest {
//
//    @Mock
//    private UserRepository mockUserRepository;
//
//    private UserDomainService userServiceUnderTest;
//    private User user;
//
//    @BeforeAll
//    public void setUp() {
//        initMocks(this);
//        userServiceUnderTest = new UserDomainService(mockUserRepository);
//        user = User.builder()
//                .username("test-user")
//                .password("password")
//                .build();
//
//        Mockito.when(mockUserRepository.save(any()))
//                .thenReturn(user);
//        Mockito.when(mockUserRepository.findByUsername(anyString()))
//                .thenReturn(user);
//    }
//
//    @Test
//    public void testFindUserByEmail() {
//        // Setup
//        final String username = "test-user";
//
//        // Run the test
//        final User result = userServiceUnderTest.findUserByUsername(username);
//
//        // Verify the results
//        assertEquals(username, result.getUsername());
//    }
//
//    @Test
//    public void testSaveUser() {
//        // Setup
//        final String username = "test-user";
//
//        // Run the test
//        User result = userServiceUnderTest.saveUser(User.builder().build());
//
//        // Verify the results
//        assertEquals(username, result.getUsername());
//    }
//}