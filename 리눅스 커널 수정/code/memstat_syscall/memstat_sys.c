#include <linux/kernel.h>
#include <linux/syscalls.h>
#include <linux/random.h>
#include <linux/slab.h>
#include <linux/mm.h>
#include <linux/list.h>
#include <linux/cpuset.h>
#include <linux/memcontrol.h>
#include <linux/rmap.h>
#include <linux/swap.h>

// #define prev_page(p) list_entry((p)->lru.prev, struct page, lru)

SYSCALL_DEFINE1(memstat_syscall, 
	int*, mem_stats_user)
{
	int* mem_stats = kmalloc(sizeof(int)*9, GFP_KERNEL);
	struct pglist_data *current_pglist = NULL; 
	struct lruvec *lruvec = NULL; 
	// struct mem_cgroup *memcg = NULL; 
	// struct mem_cgroup_per_node *mz; 
	struct list_head *in, *n;
	struct page* temp_page;
	int i;
	// struct mm_struct *mm = current->mm;
	int file_act=0, file_inact=0, anon_act=0, anon_inact=0, ref_act=0, ref_inact=0;

	for (i = 0; i < MAX_NUMNODES; i++) { 
		if (NODE_DATA(i) == NULL) continue;   
		current_pglist = NODE_DATA(i);
		if (current_pglist->node_present_pages == 0) { 
			printk(KERN_ALERT "Node-%d does not have any pages.\n", i); 
			continue; 
		} 
		// printk("node_present_pages : %lu\n", current_pglist->node_present_pages);
		lruvec = &current_pglist->__lruvec;

		spin_lock_irq(&lruvec->lru_lock);
		// memcg = get_mem_cgroup_from_mm(NULL); 

		// mz = mem_cgroup_nodeinfo(memcg, current_pglist->node_id); 
		// lruvec = &mz->lruvec;   
		list_for_each_safe(in, n, &lruvec->lists[LRU_ACTIVE_FILE]) {
			temp_page = list_entry(in, struct page, lru);
			if(PageReferenced(temp_page)) {
				ref_act++;
			}
			file_act++;
    	}

		list_for_each_safe(in, n, &lruvec->lists[LRU_INACTIVE_FILE]) {
			temp_page = list_entry(in, struct page, lru);
			if(PageReferenced(temp_page)) {
				ref_inact++;
			}
			file_inact++;
    	}
		list_for_each_safe(in, n, &lruvec->lists[LRU_ACTIVE_ANON]) {
			temp_page = list_entry(in, struct page, lru);
			if(PageReferenced(temp_page)) {
				ref_act++;
			}
			anon_act++;
    	}
		list_for_each_safe(in, n, &lruvec->lists[LRU_INACTIVE_ANON]) {
			temp_page = list_entry(in, struct page, lru);
			if(PageReferenced(temp_page)) {
				ref_inact++;
			}
			anon_inact++;
    	}

		spin_unlock_irq(&lruvec->lru_lock); 
	}

	mem_stats[0] = file_act;
	mem_stats[1] = file_inact;
	mem_stats[2] = anon_act;
	mem_stats[3] = anon_inact;
	mem_stats[4] = ref_act;
	mem_stats[5] = ref_inact;
	mem_stats[6] = promote_count;
	mem_stats[7] = demote_count;
	mem_stats[8] = evict_count;

	if(copy_to_user(mem_stats_user, mem_stats, sizeof(int)*9) != 0) {
		printk(KERN_INFO "copy_to_user Error!\n");
		return 1;
	}

	kfree(mem_stats);
	return 0;

}

