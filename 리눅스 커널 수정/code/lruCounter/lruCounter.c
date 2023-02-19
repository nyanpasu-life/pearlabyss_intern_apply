#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/module.h>
#include <linux/sched.h>
#include <linux/mm.h>
#include <linux/list.h>
#include <linux/cpuset.h>
#include <linux/memcontrol.h>
#include <linux/page_ref.h>

MODULE_DESCRIPTION("Simple kernel timer");
MODULE_AUTHOR("SCE394");
MODULE_LICENSE("GPL");

//int access_counter =0;

#define prev_page(p)	list_entry((p)->lru.prev, struct page, lru)
#define next_page(p)	list_entry((p)->lru.next, struct page, lru)
 
void count_ref_c_in_lru(struct list_head *src)
{
	//int page_count = 0;
	struct page *iter_page;
	struct page *victim_page;
	struct list_head *victim_head;
	//int iternum=0;
	//int min_ref=0;

	struct list_head *i, *tmp;
	list_for_each_safe(i, tmp, src) {
		iter_page = list_entry(i, struct page, lru);
		if(PageReferenced(iter_page)){
			if(iter_page->my_ref_c <10000){
				iter_page->my_ref_c ++;
			}
		}
		else{
			if(iter_page->my_ref_c >-10000){
				iter_page->my_ref_c --;
			}
		}
	}
}

void ref_counter_watchdog(void)
{
	struct pglist_data *current_pglist = NULL;
	struct lruvec *lruvec = NULL;
	struct mem_cgroup *memcg = NULL;
	struct mem_cgroup_per_node *mz;
	int i;
 
	for (i = 0; i < MAX_NUMNODES; i++) {
		if (NODE_DATA(i) == NULL)
			continue;
 
		current_pglist = NODE_DATA(i);
 
		if (current_pglist->node_present_pages == 0) {
			printk(KERN_ALERT "Node-%d does not have any pages.\n", i);
			continue;
		}

		memcg = get_mem_cgroup_from_mm(NULL);
		//mz = mem_cgroup_nodeinfo(memcg, current_pglist->node_id);

		lruvec = mem_cgroup_lruvec(memcg, current_pglist);
 
		spin_lock(&lruvec->lru_lock);
		
		
		//printk("========== LRU_ACTIVE_FILE ============\n");
		count_ref_c_in_lru(&lruvec->lists[LRU_ACTIVE_FILE]);
		//printk("========== LRU_INACTIVE_FILE ============\n");
		count_ref_c_in_lru(&lruvec->lists[LRU_INACTIVE_FILE]);
		//printk("========== LRU_ACTIVE_ANON ============\n");
		count_ref_c_in_lru(&lruvec->lists[LRU_ACTIVE_ANON]);
		//printk("========== LRU_INACTIVE_ANON ============\n");
		count_ref_c_in_lru(&lruvec->lists[LRU_INACTIVE_ANON]);
		//printk("========== LRU_UNEVICTABLE ============\n");
		count_ref_c_in_lru(&lruvec->lists[LRU_UNEVICTABLE]);
		//printk("\n\n\n");
		
 
		spin_unlock(&lruvec->lru_lock);
	}
}

#define TIMER_TIMEOUT	1
unsigned long seconds = 1;
unsigned long repeatTime;

static struct timer_list timer;

static void timer_handler(struct timer_list *tl)
{
	ref_counter_watchdog();
	//printk("test for timer\n");

	/* TODO 2: rechedule timer */
	//mod_timer(&timer, jiffies + repeatTime);
	mod_timer(&timer, jiffies + seconds * HZ);
}	

static int __init timer_init(void)
{
	/* TODO 1: initialize timer */
	timer_setup(&timer, timer_handler, 0);
	//repeatTime = msecs_to_jiffies(50);

	/* TODO 1: schedule timer for the first time */
	//mod_timer(&timer, jiffies + repeatTime);
	mod_timer(&timer, jiffies + seconds * HZ);

	return 0;
}

static void __exit timer_exit(void)
{

	/* TODO 1: cleanup; make sure the timer is not running after we exit */
	del_timer_sync(&timer);
}

module_init(timer_init);
module_exit(timer_exit);