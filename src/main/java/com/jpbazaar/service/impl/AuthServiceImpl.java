package com.jpbazaar.service.impl;

import com.jpbazaar.dto.request.LoginRequest;
import com.jpbazaar.dto.request.RegisterRequest;
import com.jpbazaar.dto.response.AuthResponse;
import com.jpbazaar.entity.Cart;
import com.jpbazaar.entity.Role;
import com.jpbazaar.entity.User;
import com.jpbazaar.entity.Wishlist;
import com.jpbazaar.exception.DuplicateResourceException;
import com.jpbazaar.repository.CartRepository;
import com.jpbazaar.repository.UserRepository;
import com.jpbazaar.repository.WishlistRepository;
import com.jpbazaar.security.JwtTokenProvider;
import com.jpbazaar.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(
            UserRepository userRepository,
            CartRepository cartRepository,
            WishlistRepository wishlistRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider
    ) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.wishlistRepository = wishlistRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("User already exists with email: " + request.email());
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        // Auto-create Cart and Wishlist for newly registered customer
        Cart cart = Cart.builder().user(savedUser).build();
        cartRepository.save(cart);

        Wishlist wishlist = Wishlist.builder().user(savedUser).build();
        wishlistRepository.save(wishlist);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String token = tokenProvider.generateToken(authentication);

        return AuthResponse.of(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                tokenProvider.getExpirationMs()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String token = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new DuplicateResourceException("User not found: " + request.email()));

        return AuthResponse.of(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                tokenProvider.getExpirationMs()
        );
    }
}
