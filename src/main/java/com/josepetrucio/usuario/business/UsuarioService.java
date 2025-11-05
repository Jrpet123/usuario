package com.josepetrucio.usuario.business;

import com.josepetrucio.usuario.business.converter.UsuarioConverter;
import com.josepetrucio.usuario.business.dto.UsuarioDTO;
import com.josepetrucio.usuario.infrastructure.entity.Usuario;
import com.josepetrucio.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        //usuario = usuarioRepository.save(usuario);
        return usuarioConverter.paraUsuarioDTO(
                usuarioRepository.save(usuario)
        );
    }
}
