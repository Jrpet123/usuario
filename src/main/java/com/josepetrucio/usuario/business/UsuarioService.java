package com.josepetrucio.usuario.business;

import com.josepetrucio.usuario.business.converter.UsuarioConverter;
import com.josepetrucio.usuario.business.dto.UsuarioDTO;
import com.josepetrucio.usuario.infrastructure.entity.Usuario;
import com.josepetrucio.usuario.infrastructure.exceptions.ConflictException;
import com.josepetrucio.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.josepetrucio.usuario.infrastructure.repository.UsuarioRepository;
import com.josepetrucio.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        //usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioDTO(
                usuarioRepository.save(usuario)
        );
    }
    public void emailExiste(String email){
        try{
            boolean existe = verificaEmailExistente(email);
            if(existe){
                throw new ConflictException("email ja cadastrado" + email);
            }
        } catch (ConflictException e) {
            throw new ConflictException("email ja cadastrado", e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email){ return usuarioRepository.existsByEmail(email);
    }

    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado" + email));

    }
    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);

    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO dto){
        //aqui buscamos o email do usuario atraves do token (tirando a obrigatoriedade do email)
        String email = jwtUtil.extractUsername(token.substring(7));

        //criptografa a senha sem conflitos de duplicidade de criptografia caso o user nao passe a senha
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

        //busca os dados do usuario no db
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado " + email)
                );

        //mescla os dados da requisicao DTO com o db
        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

        //criptografa a senha novamente
        //usuario.setSenha(passwordEncoder.encode(usuario.getPassword())); -> conflito de criptografias

        //salva os dados convertidos e pega o retorno e converte para UsuarioDTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

    }
}
