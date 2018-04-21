package io.netty.util.concurrent;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleThreadPoolExecutorTest {


    @Test
    public void workTest() {
        Executor executor = new SimpleThreadPoolExecutor(2);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(1);
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(2);
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(3);
            }
        });


    }


    public class SimpleThreadPoolExecutor implements Executor {

        private Queue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();
        private List<Worker> workers = new ArrayList<Worker>();

        final private int coreSize;
        private int size = 0;
        public SimpleThreadPoolExecutor(int coreSize) {
            this.coreSize = coreSize;
        }

        @Override
        public void execute(Runnable command) {
            if (size ++ <= coreSize) {
                Worker worker = new Worker(command);
                workers.add(worker);
                new Thread(worker).start();
            } else {
                addTask(command);
            }
        }

        private void addTask(Runnable command) {
            tasks.offer(command);
        }


        // 工作者线程
        public class Worker implements Runnable {
            private Thread thread;

            private Runnable task;

            public Worker(Runnable task) {
                this.task = task;
            }

            @Override
            public void run() {
                thread = Thread.currentThread();
                while (true) {
                    try {
                        if (task != null)
                            task.run();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    task = tasks.poll();
                }
            }

            public Thread thread() {
                return thread;
            }
        }
    }
}
