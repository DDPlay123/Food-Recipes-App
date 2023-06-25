package mai.project.foody.util

import androidx.recyclerview.widget.DiffUtil
import mai.project.foody.models.Result

class RecipesDiffUtil(
    private val oldList: List<Result>,
    private val newList: List<Result>
): DiffUtil.Callback() {

    // 返回舊列表的大小。
    override fun getOldListSize(): Int = oldList.size

    // 返回新列表的大小。
    override fun getNewListSize(): Int = newList.size

    // 用於比較兩個列表中的項目是否相同。
    // areItemsTheSame 方法用於檢查在給定位置上的兩個項目是否代表同一個物件。
    // 通常，你可以通過比較唯一標識符（例如ID）或物件引用來判斷兩個項目是否相同。
    // 如果這兩個項目相同（代表同一個物件），則返回true；否則返回false。
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] === newList[newItemPosition]

    // 用於比較兩個列表中的項目的'內容'是否相同。
    // areContentsTheSame 方法用於檢查在給定位置上的兩個項目的內容是否相同。
    // 這通常涉及檢查項目的各個屬性是否相等。如果兩個項目的內容相同，則返回true；否則返回false。
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}