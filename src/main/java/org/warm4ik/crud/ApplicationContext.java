package org.warm4ik.crud;

import org.warm4ik.crud.controller.LabelController;
import org.warm4ik.crud.controller.PostController;
import org.warm4ik.crud.controller.WriterController;
import org.warm4ik.crud.repository.LabelRepository;
import org.warm4ik.crud.repository.PostRepository;
import org.warm4ik.crud.repository.WriterRepository;
import org.warm4ik.crud.repository.impl.JdbcLabelRepository;
import org.warm4ik.crud.repository.impl.JdbcPostRepository;
import org.warm4ik.crud.repository.impl.JdbcWriterRepository;
import org.warm4ik.crud.service.LabelService;
import org.warm4ik.crud.service.PostService;
import org.warm4ik.crud.service.WriterService;
import org.warm4ik.crud.view.LabelView;
import org.warm4ik.crud.view.PostView;
import org.warm4ik.crud.view.WriterView;


public class ApplicationContext {
    private final WriterRepository writerRepository = new JdbcWriterRepository();
    private final PostRepository postRepository = new JdbcPostRepository();
    private final LabelRepository labelRepository = new JdbcLabelRepository();

    private final WriterService writerService = new WriterService(writerRepository);
    private final PostService postService = new PostService(postRepository);
    private final LabelService labelService = new LabelService(labelRepository);

    private final WriterController writerController = new WriterController(writerService);
    private final PostController postController = new PostController(postService);
    private final LabelController labelController = new LabelController(labelService);

    private final WriterView writerView = new WriterView(writerController, postController);
    private final PostView postView = new PostView(postController, writerController, labelController);
    private final LabelView labelView = new LabelView(labelController);

    public void writerRun() {
        writerView.run();
    }

    public void postRun() {
        postView.run();
    }

    public void labelRun() {
        labelView.run();
    }
}