package org.warm4ik.crud.controller;

import lombok.RequiredArgsConstructor;
import org.warm4ik.crud.model.Post;
import org.warm4ik.crud.service.PostService;

import java.util.List;

@RequiredArgsConstructor
public class PostController {
    private final PostService postService;


    public void createPost(Post post) {
        postService.savePost(post);
    }

    public Post getPostById(Integer postId) {
        return postService.getPostById(postId);
    }

    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    public void updatePost(Post existingPost) {
        postService.updatePost(existingPost);
    }

    public void deletePost(Integer postId) {
        postService.deletePost(postId);
    }
}
