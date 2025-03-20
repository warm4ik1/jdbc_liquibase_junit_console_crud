package org.warm4ik.crud.service;

import lombok.RequiredArgsConstructor;
import org.warm4ik.crud.model.Post;
import org.warm4ik.crud.repository.PostRepository;

import java.util.List;

@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Post getPostById(Integer postId) {
        return postRepository.getById(postId);
    }

    public List<Post> getAllPosts() {
        return postRepository.getAll();
    }

    public Post updatePost(Post existingPost) {
        return postRepository.update(existingPost);
    }

    public void deletePost(Integer postId) {
        postRepository.deleteById(postId);
    }
}
