package pw.parcial2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import jakarta.servlet.DispatcherType;
import pw.parcial2.repository.UsuarioRepo;

@Configuration
public class Seguridad {
	@Bean
	SecurityFilterChain filtro(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
				.requestMatchers("/login", "/403", "/style01.jspf", "/estilos.css").permitAll()
				.requestMatchers("/admin").hasRole("ADMIN")
				.requestMatchers("/medico").hasRole("MEDICO")
				.requestMatchers("/paciente").hasRole("PACIENTE")
				.requestMatchers("/api/usuarios/**").hasRole("ADMIN")
				.requestMatchers(org.springframework.http.HttpMethod.GET, "/api/**").authenticated()
				.requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/consultas/*/horario").hasRole("PACIENTE")
				.requestMatchers("/api/**").hasRole("ADMIN")
				.anyRequest().authenticated())
			.exceptionHandling(error -> error
				.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), request -> request.getServletPath().startsWith("/api/"))
				.accessDeniedPage("/403"))
			.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
				.defaultSuccessUrl("/", true).failureUrl("/login?error").permitAll())
			.logout(logout -> logout.logoutSuccessUrl("/login").permitAll());
		return http.build();
	}

	@Bean
	UserDetailsService usuarios(UsuarioRepo repo) {
		return username -> repo.findByUsername(username)
			.map(usuario -> User.withUsername(usuario.getUsername()).password(usuario.getPassword()).roles(usuario.getRol()).build())
			.orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(username));
	}

	@Bean
	PasswordEncoder codificador() {
		return new BCryptPasswordEncoder();
	}
}
