package com.elixir.springframework.fuse.security;

/**
 * Created by elixir on 3/17/16.
 */
//@Configuration
//@EnableWebSecurity @Order(1)
public class SecurityConfiguration// extends WebSecurityConfigurerAdapter
        {
    //@Override
//    protected void configure(HttpSecurity http) throws Exception {
//        System.out.println("http"+http);
//        http
//                .authorizeRequests()
//                //.antMatchers("/", "/home","/admin/user/create").permitAll()
//                .antMatchers("/**", "/").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                //.loginPage("/login")
//                .permitAll()
//                .and()
//                .logout()
//                .permitAll();
//    }
//
//    //@Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        System.out.println("cccccc"+auth);
//        auth
//                .inMemoryAuthentication()
//                .withUser("user").password("password").roles("USER");
//    }
}
