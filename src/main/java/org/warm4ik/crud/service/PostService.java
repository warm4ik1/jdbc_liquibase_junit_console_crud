package org.warm4ik.crud.service;

import lombok.RequiredArgsConstructor;
import org.warm4ik.crud.model.Post;
import org.warm4ik.crud.repository.PostRepository;

import java.util.List;

@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public void createPost(Post post) {
        postRepository.save(post);
    }

    public Post getPostById(Integer postId) {
        return postRepository.getById(postId);
    }

    public List<Post> getAllPosts() {
        return postRepository.getAll();
    }

    public void updatePost(Post existingPost) {
        postRepository.update(existingPost);
    }

    public void deletePost(Integer postId) {
        postRepository.deleteById(postId);
    }
}
