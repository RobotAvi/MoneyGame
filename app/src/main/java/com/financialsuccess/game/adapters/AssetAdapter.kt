package com.financialsuccess.game.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.financialsuccess.game.databinding.ItemAssetBinding
import com.financialsuccess.game.models.Asset
import java.text.NumberFormat
import java.util.*

class AssetAdapter(
    private val assets: List<Asset>,
    private val onAssetClick: (Int) -> Unit
) : RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    
    inner class AssetViewHolder(private val binding: ItemAssetBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(asset: Asset, position: Int) {
            binding.tvAssetName.text = asset.name
            binding.tvAssetType.text = getAssetTypeText(asset.type)
            binding.tvAssetValue.text = "Стоимость: ${currencyFormat.format(asset.value)}"
            binding.tvCashFlow.text = "Поток: +${currencyFormat.format(asset.cashFlow)}/мес"
            
            if (asset.shares > 1) {
                binding.tvShares.text = "Количество: ${asset.shares}"
            } else {
                binding.tvShares.text = ""
            }
            
            binding.root.setOnClickListener {
                onAssetClick(position)
            }
        }
        
        private fun getAssetTypeText(type: com.financialsuccess.game.models.AssetType): String {
            return when (type) {
                com.financialsuccess.game.models.AssetType.REAL_ESTATE -> "Недвижимость"
                com.financialsuccess.game.models.AssetType.STOCKS -> "Акции"
                com.financialsuccess.game.models.AssetType.BUSINESS -> "Бизнес"
                com.financialsuccess.game.models.AssetType.CRYPTO -> "Криптовалюта"
                com.financialsuccess.game.models.AssetType.BONDS -> "Облигации"
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val binding = ItemAssetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AssetViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        holder.bind(assets[position], position)
    }
    
    override fun getItemCount() = assets.size
}